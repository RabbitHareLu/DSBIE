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
import java.util.Objects;
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
            newMenu.setIcon(ImageLoadUtil.getInstance().getNewIcon());
            JMenuItem exitMenu = new JMenuItem("退出");
            exitMenu.setIcon(ImageLoadUtil.getInstance().getExitIcon());
            fileMenu.add(newMenu);
            fileMenu.add(exitMenu);

            JMenu fontMenu = new JMenu("字体");
            fontMenu.setIcon(ImageLoadUtil.getInstance().getFontIcon());
            JMenu fontNameMenu = new JMenu("字体名称");
            fontNameMenu.setIcon(ImageLoadUtil.getInstance().getFontNameIcon());
            JMenu fontSizeMenu = new JMenu("字体大小");
            fontSizeMenu.setIcon(ImageLoadUtil.getInstance().getFontSizeIcon());
            JMenu fontStyleMenu = new JMenu("字体样式");
            fontStyleMenu.setIcon(ImageLoadUtil.getInstance().getFontStyleIcon());
            settingsMenu.add(fontMenu);
            fontMenu.add(fontNameMenu);
            fontMenu.add(fontSizeMenu);
            fontMenu.add(fontStyleMenu);

            JMenuItem newFolder = new JMenuItem("新建文件夹");
            newFolder.setIcon(ImageLoadUtil.getInstance().getNewFolderIcon());
            JMenu newJDBCConnection = new JMenu("新建JDBC连接");
            JMenuItem about = new JMenuItem("关于");
            about.setIcon(ImageLoadUtil.getInstance().getAboutIcon());
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
        ButtonGroup fontSizeGroup = new ButtonGroup();
        /*for (int i = 10; i <= 30; i++) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(i + "");
            if (Objects.equals(fontSize, i)) {
                jCheckBoxMenuItem.setSelected(true);
            }

            jCheckBoxMenuItem.addActionListener(new UpdateFontSizeAction());
            fontSizeGroup.add(jCheckBoxMenuItem);
            fontSizeMenu.add(jCheckBoxMenuItem);
        }*/
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
