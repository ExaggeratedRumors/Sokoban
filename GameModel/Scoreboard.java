package GameModel;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Klasa panelu wyników
 */
public class Scoreboard extends JPanel {

    /**
     * Klasa pojedynczego wyniku
     */
    private class Score{
        /**
         * Nazwa gracza
         */
        private String nickName;
        /**
         * Wynik
         */
        private int score;

        /**
         * Konstruktor
         * @param nickName
         * @param score
         */
        public Score(String nickName, int score){
            this.nickName=nickName;
            this.score=score;
        }

        /**
         * Metoda pobierająca nazwę gracza
         * @return nickname
         */
        public String getNickName() {
            return nickName;
        }

        /**
         * Metoda pobierająca liczbę punków
         * @return
         */
        public int getScore() {
            return score;
        }
    }

    /**
     * Lista obiektów klasy wyników
     */
    ArrayList<Score> scoreLadder;
    /**
     * Liczba pozycji
     */
    public int numberOfScores;

    /**
     * Konstruktor
     * @param panelWidth szerokość okna
     * @param panelHeight wysokość okna
     * @param menuListener listener menu gry
     */
    public Scoreboard(int panelWidth, int panelHeight, ActionListener menuListener) {
        numberOfScores=15;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        scoreLadder = new ArrayList<>();
        loadScoreboard();
        add(createTitle(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createBackButton(menuListener), BorderLayout.SOUTH);
        setBackground(new Color(12629968));
        setVisible(true);
    }

    /**
     * Metoda pobierająca wyniki z pliku
     */
    private void loadScoreboard() {
        try {
            File xmlInputFile = new File("Config\\scoreboard.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlInputFile);
            doc.getDocumentElement().normalize();
            NodeList nl = doc.getElementsByTagName("index");
            for (int temp = 0; temp < nl.getLength(); temp++) {
                Node node = nl.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    scoreLadder.add(new Score(element.getElementsByTagName("nickName").item(0).getTextContent(), Integer.parseInt(element.getElementsByTagName("score").item(0).getTextContent())));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(this, "Nie znaleziono pliku", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda zapisująca wynik do pliku
     */
    private void saveScore(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element source = doc.createElement("scoreboard");
            doc.appendChild(source);
            for(int i = 0 ; i < numberOfScores ; i++) {
                if(scoreLadder.size() <= i) break;
                Element index = doc.createElement("index");
                source.appendChild(index);
                index.setAttribute("id", Integer.toString(i));
                Element nickName = doc.createElement("nickName");
                nickName.appendChild(doc.createTextNode(scoreLadder.get(i).getNickName()));
                index.appendChild(nickName);
                Element score = doc.createElement("score");
                score.appendChild(doc.createTextNode(Integer.toString(scoreLadder.get(i).getScore())));
                index.appendChild(score);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource docSource = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("Config\\scoreboard.xml"));
            transformer.transform(docSource, result);
        }
        catch (Exception e){}
}

    /**
     * Metoda tworząca listę wyników
     */
    private JTable createTable() {
        sortLadder();
        saveScore();
        Vector<Vector> rows = new Vector<>();
        for (int i = 0; i < numberOfScores; i++) {
            Vector<String> row = new Vector<>();
            String rowNumber = "nr."+Integer.toString(i + 1);
            row.add(rowNumber);
            row.add(scoreLadder.get(i).getNickName());
            row.add(Integer.toString(scoreLadder.get(i).getScore()));
            rows.add(row);
        }
        Vector<String> cols = new Vector<>();
        cols.addElement("nr");
        cols.addElement("Nazwa Gracza");
        cols.addElement("Liczba punktów");
        JTable scoreLadder = new JTable(rows, cols);
        scoreLadder.setBackground(new Color(12629968));
        scoreLadder.setEnabled(false);
        scoreLadder.getTableHeader().setReorderingAllowed(false);
        setFont(new Font("Stencil", Font.PLAIN, 12));
        return scoreLadder;
    }

    /**
     * Metoda tworząca przycisk powrotu do menu
     * @param menuListener listener menu
     * @return przycisk powrotu do menu
     */
    private JButton createBackButton(ActionListener menuListener) {
        JButton backButton = new JButton("Powrót do menu");
        backButton.setFocusable(false);
        backButton.addActionListener(menuListener);
        backButton.setActionCommand("backButton2");
        return backButton;
    }

    /**
     * Metoda tworząca tytuł okna
     * @return tytuł okna
     */
    private JLabel createTitle() {
        JLabel title = new JLabel("<html><br>Lista najlepszych wyników<br><br></html>");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 20));
        title.setForeground(new Color(7546379));
        return title;
    }

    /**
     * Metoda sortująca listę wyników
     */
    public void sortLadder() {
        Collections.sort(scoreLadder, (pair, t1) -> {
            if(pair.getScore()<t1.getScore()) return 1;
            if(pair.getScore()>t1.getScore()) return -1;
            else return 0;
        });
    }

    /**
     * Metoda dodająca nowy wynik
     * @param nickname nazwa gracza
     * @param score wynik
     */
    public void newScore(String nickname, int score){
        scoreLadder.add(new Score(nickname, score));
        sortLadder();
        saveScore();
    }

    /**
     * Pobieranie najniższego wyniku z listy
     */
    public int getLowestScore(){
        return scoreLadder.get(numberOfScores-1).getScore();
    }
}

