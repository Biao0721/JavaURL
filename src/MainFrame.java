import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;

public class MainFrame extends JFrame implements ActionListener {
    private JTextPane jTextPane = new JTextPane();    // 显示爬取结果
    private JTextField jTextField = new JTextField(10); // 输入爬取网址
    private JButton jButtonRequest = new JButton("爬取");
    private JButton jButtonText = new JButton("文本");
    private JButton jButtonHighLight = new JButton("高亮");
    private JButton getjButtonRequestAllURL = new JButton("爬取所有文件网址");
    private JScrollPane jScrollPane = new JScrollPane();
    private JList jList = new JList();

    private String[] stringsURL = null;
    private String url = null;
    private JavaURL javaURL = new JavaURL();
    private String[] sensitiveWords = null;

    public MainFrame() {
        setSensitiveWords();
        setStringsURL();

        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        JPanel jPanel3 = new JPanel();

        // 配置页面布局
        this.setLayout(new BorderLayout());
        this.add(jPanel1, BorderLayout.WEST);
        this.add(jPanel3, BorderLayout.CENTER);
        this.setTitle("JavaURL");
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        jList.setListData(stringsURL);

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(jPanel2, BorderLayout.NORTH);
        jPanel1.add(jScrollPane, BorderLayout.CENTER);
        jPanel1.add(getjButtonRequestAllURL, BorderLayout.SOUTH);

        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(jTextField, BorderLayout.NORTH);
        jPanel2.add(jButtonRequest, BorderLayout.WEST);
        jPanel2.add(jButtonText, BorderLayout.CENTER);
        jPanel2.add(jButtonHighLight, BorderLayout.EAST);

        jPanel3.setLayout(new GridLayout(1, 1));
        jPanel3.add(new JScrollPane(jTextPane));

        // 配置组件信息
        jButtonRequest.addActionListener(this);
        jButtonHighLight.addActionListener(this);
        jButtonText.addActionListener(this);
        getjButtonRequestAllURL.addActionListener(this);

        jScrollPane.setPreferredSize(new Dimension(100, 100));
        jScrollPane.setViewportView(jList);

        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                jTextField.setText(jList.getSelectedValue().toString());
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == jButtonRequest) {
            // 获取网页所有HTML代码

            url = jTextField.getText();
            jTextPane.setText("");
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(null, "未输入网址", "Error",JOptionPane.ERROR_MESSAGE);
            } else {
                insertDocument(javaURL.httpRequest("http://" + url), Color.BLACK);
            }

        } else if (actionEvent.getSource() == jButtonText) {
            // 获取网页文本

            url = jTextField.getText();
            jTextPane.setText("");
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(null, "未输入网址", "Error",JOptionPane.ERROR_MESSAGE);
            } else {
                insertDocument(javaURL.StripHT(javaURL.httpRequest("http://" + url)), Color.BLACK);
            }

        } else if (actionEvent.getSource() == jButtonHighLight) {
            // 对网页文本中的敏感词高亮显示

            url = jTextField.getText();
            jTextPane.setText("");
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(null, "未输入网址", "Error",JOptionPane.ERROR_MESSAGE);
            } else {
                getSensitiveWord(javaURL.StripHT(javaURL.httpRequest("http://" + url)));
            }

        } else if (actionEvent.getSource() == getjButtonRequestAllURL) {
            // 爬取所有网页的内容，并保存相应信息
            saveAllURL();
        }
    }

    private void setSensitiveWords() {
        try {
            File file = new File("E:\\JavaURL\\SensitiveWords.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            sensitiveWords = bufferedReader.readLine().toString().split("#");
//            for (String sensitiveWord: sensitiveWords) {
//                System.out.println(sensitiveWord);
//            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setStringsURL() {
        try {
            File file = new File("E:\\JavaURL\\HTML.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            stringsURL = bufferedReader.readLine().toString().split("#");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void getSensitiveWord(String text) {
        for (String sensitiveWord: sensitiveWords) {
            text = text.replaceAll(sensitiveWord, ("#" + sensitiveWord + "#"));
//            System.out.println(sensitiveWord + text);
        }
        String[] strings = text.split("#");
        for (String string: strings) {
            if (Arrays.asList(sensitiveWords).contains(string)) {
                insertDocument(string, Color.BLUE);
            } else {
                insertDocument(string, Color.BLACK);
            }
        }
    }

    private void saveAllURL() {
        String pathHTML = "E:\\JavaURL\\HTML\\";
        String pathSensitiveWordsHTML = "E:\\JavaURL\\SensitiveWordsHTML\\";
        for (String url: stringsURL) {
            try {
                String request = javaURL.StripHT(javaURL.httpRequest("http://" + url));

                FileOutputStream fileOutputStreamHTML = new FileOutputStream(pathHTML + url + ".txt");
                FileOutputStream fileOutputStreamSensitiveWordsHTML = new FileOutputStream(pathSensitiveWordsHTML + url + ".txt");

                OutputStreamWriter outputStreamWriterHTML = new OutputStreamWriter(fileOutputStreamHTML, "UTF-8");
                OutputStreamWriter outputStreamWriterSensitiveWordsHTML = new OutputStreamWriter(fileOutputStreamSensitiveWordsHTML, "UTF-8");

                outputStreamWriterHTML.write(request);
                outputStreamWriterHTML.flush();
                for (String sensitiveWord: sensitiveWords) {
                    outputStreamWriterSensitiveWordsHTML.write(sensitiveWord + ": " + count(request, sensitiveWord) + "\n");
                    outputStreamWriterSensitiveWordsHTML.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int count(String text, String findStr) {
        int num = 0;
        String tmp = text;
        while (tmp.contains(findStr)) {
            tmp = tmp.substring(tmp.indexOf(findStr) + findStr.length());
            num ++;
        }
        return num;
    }

    public void insertDocument(String text, Color textColor){
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(simpleAttributeSet, textColor);
        StyleConstants.setFontSize(simpleAttributeSet, 12);
        Document document = jTextPane.getStyledDocument();
        try {
            document.insertString(document.getLength(), text, simpleAttributeSet);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}