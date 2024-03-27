package com.dsbie.frontend.frame;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:16
 */
@Slf4j
@Data
public class DsbieJFrame extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    public static final String TITLE = "DSBIE V1.0.0";

    public static JMenuBar jMenuBar = null;
    public static JSplitPane rootJSplitPane = null;
    public static JTree jTree = null;

    private ArrayList<CompletableFuture<Void>> completableFutureArrayList = new ArrayList<>();

    public DsbieJFrame() {
        setIconImage(ImageLoadUtil.getInstance().getLogoIcon().getImage());
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
        setVisible(true);
    }

    private void init() {
        initMenu();

        completableFutureArrayList.add(CompletableFuture.runAsync(() -> {
            JTextArea jTextArea = new JTextArea();
            SwingUtilities.invokeLater(() -> {
                add(jTextArea, BorderLayout.CENTER);
                validate();
            });
        }));

    }

    private void initMenu() {
        completableFutureArrayList.add(CompletableFuture.runAsync(() -> {
            jMenuBar = new JMenuBar();

            JMenu fileMenu = new JMenu("文件");
            JMenu editMenu = new JMenu("编辑");
            JMenu settingsMenu = new JMenu("设置");
            JMenu helpMenu = new JMenu("帮助");
            jMenuBar.add(fileMenu);
            jMenuBar.add(editMenu);
            jMenuBar.add(settingsMenu);
            jMenuBar.add(helpMenu);

            JMenu newMenu = new JMenu("新建");
            JMenuItem exitMenu = new JMenuItem("退出");
            fileMenu.add(newMenu);
            fileMenu.add(exitMenu);

            JMenu fontMenu = new JMenu("字体");
            JMenu fontNameMenu = new JMenu("字体名称");
            JMenu fontSizeMenu = new JMenu("字体大小");
            JMenu fontStyleMenu = new JMenu("字体样式");
            settingsMenu.add(fontMenu);
            fontMenu.add(fontNameMenu);
            fontMenu.add(fontSizeMenu);
            fontMenu.add(fontStyleMenu);

            JMenuItem newFolder = new JMenuItem("新建文件夹");
            JMenu newJDBCConnection = new JMenu("新建JDBC连接");
            JMenuItem about = new JMenuItem("关于");
            newMenu.add(newFolder);
            newMenu.add(newJDBCConnection);
            helpMenu.add(about);

            setAboutAction(about);

            SwingUtilities.invokeLater(() -> {
                add(jMenuBar, BorderLayout.NORTH);
                validate();
            });
        }));
    }

    private void initFontMenu() {

    }

    private void setAboutAction(JMenuItem about) {
        about.addActionListener(e -> {
            JLabel titleLabel = new JLabel(DsbieJFrame.TITLE);
            titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
            JOptionPane.showMessageDialog(Main.dsbieJFrame,
                    new Object[]{
                            titleLabel,
                            "DB Stream Batch Import Export",
                            " ",
                            "Copyright 2024-" + Year.now() + " Liu sl, Wang cg",
                    },
                    "About", JOptionPane.PLAIN_MESSAGE);

        });
    }
}
