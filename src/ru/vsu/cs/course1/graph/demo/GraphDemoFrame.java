package ru.vsu.cs.course1.graph.demo;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import ru.vsu.cs.course1.graph.Graph;
import ru.vsu.cs.course1.graph.GraphUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphDemoFrame extends JFrame {
    private JTabbedPane tabbedPaneMain;
    private JPanel panelMain;
    private JPanel panelGraphTab;
    private JTextArea textAreaSystemOut;
    private JPanel panelGraphPainterContainer;

    private JTextArea textAreaGraphFile;
    private JComboBox comboBoxGraphType;
    private JButton buttonCreateGraph;
    private JSplitPane splitPaneGraphTab1;
    private JSplitPane splitPaneGraphTab2;

    private JButton buttonSaveGraphSvgToFile;

    private JSpinner spinnerCountVertex;
    private JButton buttonCreateRandom;
    private JSpinner spinnerStart;
    private JSpinner spinnerEnd;
    private JButton buttonTask;


    private JFileChooser fileChooserTxtOpen;
    private JFileChooser fileChooserDotOpen;
    private JFileChooser fileChooserTxtSave;
    private JFileChooser fileChooserDotSave;
    private JFileChooser fileChooserImgSave;

    private Graph graph = null;

    private SvgPanel panelGraphPainter;


    private static class SvgPanel extends JPanel {
        private String svg = null;
        private GraphicsNode svgGraphicsNode = null;

        public void paint(String svg) throws IOException {
            String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
            SVGDocument doc = df.createSVGDocument(null, new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            svgGraphicsNode = builder.build(ctx, doc);

            this.svg = svg;
            repaint();
        }

        @Override
        public void paintComponent(Graphics gr) {
            super.paintComponent(gr);

            if (svgGraphicsNode == null) {
                return;
            }

            double scaleX = this.getWidth() / svgGraphicsNode.getPrimitiveBounds().getWidth();
            double scaleY = this.getHeight() / svgGraphicsNode.getPrimitiveBounds().getHeight();
            double scale = Math.min(scaleX, scaleY);
            AffineTransform transform = new AffineTransform(scale, 0, 0, scale, 0, 0);
            svgGraphicsNode.setTransform(transform);
            Graphics2D g2d = (Graphics2D) gr;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            svgGraphicsNode.paint(g2d);
        }
    }


    public GraphDemoFrame() {
        this.setTitle("??????????");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        splitPaneGraphTab1.setBorder(null);
        splitPaneGraphTab2.setBorder(null);

        fileChooserTxtOpen = new JFileChooser();
        fileChooserDotOpen = new JFileChooser();
        fileChooserTxtSave = new JFileChooser();
        fileChooserDotSave = new JFileChooser();
        fileChooserImgSave = new JFileChooser();
        fileChooserTxtOpen.setCurrentDirectory(new File("./files/input"));
        fileChooserDotOpen.setCurrentDirectory(new File("./files/input"));
        fileChooserTxtSave.setCurrentDirectory(new File("./files/input"));
        fileChooserDotSave.setCurrentDirectory(new File("./files/input"));
        fileChooserImgSave.setCurrentDirectory(new File("./files/output"));
        FileFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        FileFilter dotFilter = new FileNameExtensionFilter("DOT files (*.dot)", "dot");
        FileFilter svgFilter = new FileNameExtensionFilter("SVG images (*.svg)", "svg");
        //FileFilter pngFilter = new FileNameExtensionFilter("PNG images (*.png)", "png");

        fileChooserTxtOpen.addChoosableFileFilter(txtFilter);
        fileChooserDotOpen.addChoosableFileFilter(dotFilter);
        fileChooserTxtSave.addChoosableFileFilter(txtFilter);
        fileChooserDotSave.addChoosableFileFilter(dotFilter);
        fileChooserImgSave.addChoosableFileFilter(svgFilter);
        //fileChooserImgSave.addChoosableFileFilter(pngFilter);

        fileChooserTxtSave.setAcceptAllFileFilterUsed(false);
        fileChooserTxtSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserTxtSave.setApproveButtonText("Save");
        fileChooserDotSave.setAcceptAllFileFilterUsed(false);
        fileChooserDotSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserDotSave.setApproveButtonText("Save");
        fileChooserImgSave.setAcceptAllFileFilterUsed(false);
        fileChooserImgSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserImgSave.setApproveButtonText("Save");

        panelGraphPainterContainer.setLayout(new BorderLayout());
        panelGraphPainter = new SvgPanel();
        panelGraphPainterContainer.add(new JScrollPane(panelGraphPainter));


        Method[] methods = GraphvizExamples.class.getMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));

        spinnerCountVertex.setValue(7);

        buttonCreateRandom.addActionListener(e -> {

            CreateRandomGraph rnd = new CreateRandomGraph((Integer) spinnerCountVertex.getValue(), 0.07);
            /*System.out.println(rnd.getConsol());
            System.out.println();
            System.out.println(rnd.getGraphWiz());*/
            textAreaGraphFile.setText(rnd.getConsol());
            buttonCreateGraph.doClick();


        });
        buttonCreateGraph.addActionListener(e -> {
            try {
                String name = comboBoxGraphType.getSelectedItem().toString();
                Matcher matcher = Pattern.compile(".*\\W(\\w+)\\s*\\)\\s*$").matcher(name);
                matcher.find();
                String className = matcher.group(1);
                Class clz = Class.forName("ru.vsu.cs.course1.graph." + className);
                Graph graph = GraphUtils.fromStr(textAreaGraphFile.getText(), clz);
                GraphDemoFrame.this.graph = graph;
                panelGraphPainter.paint(dotToSvg(GraphUtils.toDot(graph)));
            } catch (Exception exc) {
                SwingUtils.showErrorMessageBox(exc);
            }
        });
        buttonTask.addActionListener(e -> {
            buttonCreateGraph.doClick();

            Task t = new Task(this.graph, (Integer) spinnerStart.getValue(), (Integer) spinnerEnd.getValue());

            Set<List<Integer>> roads = t.getResCycle();
            if (roads.size() == 0) {
                textAreaSystemOut.setText("???????? ???? ??????????????");
                return;
            }
            String s = "?????? ????????: \n";
            for (List<Integer> l : roads) {
                s += l.toString() + "\n";
            }

            List<Integer> belongEveryone = t.getBelongEveryone();

            if (belongEveryone.size() == 0) {
                textAreaSystemOut.setText(s + "\n?????? ????????????, ?????????????? ?????????????????????? ???????? ??????????");
                return;
            }
            s += "??????????????, ?????????????????????????? ???????? ??????????\n";
            for (Integer vertex : belongEveryone) {

                s += vertex + " ";
            }

            textAreaSystemOut.setText(s);
            try {
                panelGraphPainter.paint(dotToSvg(GraphUtils.toDotColor(graph, belongEveryone, (Integer) spinnerStart.getValue(), (Integer) spinnerEnd.getValue())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            panelGraphPainter.repaint();


        });
        /*buttonShortestCycle.addActionListener(e -> {
            buttonCreateGraph.doClick();
            Task t = new Task(this.graph);

            LinkedList<Integer> cycle = t.getResCycle();

            if (cycle.size() == 0) {
                textAreaSystemOut.setText("?????????? ??????");
                return;
            }

            String s = "?????????????????????? ?????????? ??????????: " + Integer.toString(cycle.size()) + "\n";
            s += "?????????????? ??????????: " + cycle.toString() + "\n";

            textAreaSystemOut.setText(s);


        });*/
        buttonSaveGraphSvgToFile.addActionListener(e -> {
            if (panelGraphPainter.svg == null) {
                return;
            }
            if (fileChooserImgSave.showSaveDialog(GraphDemoFrame.this) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooserImgSave.getSelectedFile().getPath();
                if (!filename.toLowerCase().endsWith(".svg")) {
                    filename += ".svg";
                }
                try (FileWriter wr = new FileWriter(filename)) {
                    wr.write(panelGraphPainter.svg);
                } catch (Exception exc) {
                    SwingUtils.showErrorMessageBox(exc);
                }
            }
        });

    }

    /**
     * ???????????????????????????? dot-???????????? ?? svg-?????????????????????? (?? ?????????????? Graphviz)
     *
     * @param dotSrc dot-????????????
     * @return svg
     * @throws IOException
     */
    private static String dotToSvg(String dotSrc) throws IOException {
        MutableGraph g = new Parser().read(dotSrc);
        return Graphviz.fromGraph(g).render(Format.SVG).toString();
    }

    /**
     * ???????????????????? ???????????????? ?? ?????????????? ???????????????????????? ???????????? ?? ???????? (textAreaSystemOut)
     *
     * @param action ?????????????????????? ????????????????
     */
    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), 10, 10));
        panelMain.setInheritsPopupMenu(true);
        tabbedPaneMain = new JTabbedPane();
        tabbedPaneMain.setName("");
        panelMain.add(tabbedPaneMain, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        panelGraphTab = new JPanel();
        panelGraphTab.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), 10, 10));
        tabbedPaneMain.addTab("????????", null, panelGraphTab, "???????????????????????? ???????????? ?? ??????????????");
        splitPaneGraphTab1 = new JSplitPane();
        splitPaneGraphTab1.setOrientation(0);
        splitPaneGraphTab1.setResizeWeight(0.75);
        panelGraphTab.add(splitPaneGraphTab1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPaneGraphTab1.setRightComponent(scrollPane1);
        textAreaSystemOut = new JTextArea();
        scrollPane1.setViewportView(textAreaSystemOut);
        splitPaneGraphTab2 = new JSplitPane();
        splitPaneGraphTab2.setResizeWeight(0.0);
        splitPaneGraphTab1.setLeftComponent(splitPaneGraphTab2);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPaneGraphTab2.setLeftComponent(panel1);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textAreaGraphFile = new JTextArea();
        textAreaGraphFile.setText("6\n6\n0 1\n1 2\n2 4\n4 3\n3 2\n3 1\n");
        scrollPane2.setViewportView(textAreaGraphFile);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxGraphType = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("??-???????? (AdjMatrixGraph)");
        defaultComboBoxModel1.addElement("??-???????? (AdjListsGraph)");
        defaultComboBoxModel1.addElement("???????????? (AdjMatrixDigraph)");
        defaultComboBoxModel1.addElement("???????????? (AdjListsDigraph)");
        comboBoxGraphType.setModel(defaultComboBoxModel1);
        panel2.add(comboBoxGraphType, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCreateGraph = new JButton();
        buttonCreateGraph.setText("?????????????????? ????????");
        panel2.add(buttonCreateGraph, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("??????-???? ????????????");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spinnerCountVertex = new JSpinner();
        panel4.add(spinnerCountVertex, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCreateRandom = new JButton();
        buttonCreateRandom.setText("????????????");
        panel4.add(buttonCreateRandom, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel4.add(spacer4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("??????????");
        panel5.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("??????????");
        panel5.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonTask = new JButton();
        buttonTask.setText("?????????????????????? ???????? ??????????");
        panel6.add(buttonTask, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel7, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spinnerStart = new JSpinner();
        panel7.add(spinnerStart, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spinnerEnd = new JSpinner();
        panel7.add(spinnerEnd, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        splitPaneGraphTab2.setRightComponent(panel8);
        buttonSaveGraphSvgToFile = new JButton();
        buttonSaveGraphSvgToFile.setText("?????????????????? ?? ????????");
        panel8.add(buttonSaveGraphSvgToFile, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelGraphPainterContainer = new JPanel();
        panelGraphPainterContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panelGraphPainterContainer, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panelGraphPainterContainer.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel8.add(spacer6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
