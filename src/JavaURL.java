import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaURL {
    public static String httpRequest(String requestURL) {
        StringBuffer stringBuffer = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        int responseCode = 200;
        String result = null;

        try {
            URL url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                bufferedReader = new BufferedReader(inputStreamReader);

                stringBuffer = new StringBuffer();
                String string = null;
                while ((string = bufferedReader.readLine()) != null) {
                    stringBuffer.append(string);
                }

                result = stringBuffer.toString();
            } else {
                result = "#ERROR# 获取网页错误，错误码为: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) { e.printStackTrace(); }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) { e.printStackTrace(); }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) { e.printStackTrace(); }

            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return result;
    }

    public static String StripHT(String html) {
        if (html.startsWith("#ERROR#")) {
            return html;
        } else {
            String txtcontent = html.replaceAll("</?[^>]+>", "");           //剔出<html>的标签
            txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", ""); //去除字符串中的空格,回车,换行符,制表符
            return txtcontent;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(StripHT(httpRequest("http://www.baidu.com")));
    }
}