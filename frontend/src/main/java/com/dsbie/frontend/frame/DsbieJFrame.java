package com.dsbie.frontend.frame;

import com.dsbie.frontend.Main;
import com.dsbie.frontend.component.FrameJPopupMenu;
import com.dsbie.frontend.component.LeftTree;
import com.dsbie.frontend.utils.CompletableFutureUtil;
import com.dsbie.frontend.utils.FontUtil;
import com.dsbie.frontend.utils.ImageLoadUtil;
import com.dsbie.rearend.KToolsContext;
import com.dsbie.rearend.api.SystemApi;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:16
 */
@Slf4j
@Getter
@Setter
public class DsbieJFrame extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    public static final String TITLE = "DSBIE V1.0.0";

    public static JMenuBar jMenuBar = null;
    public static JSplitPane rootJSplitPane = null;
    public static JTree jTree = null;
    public static JTabbedPane closableTabsTabbedPane = null;
    public static JLabel logoLabel = null;

    private String[] fontStyleArr = new String[]{"普通", "斜体", "粗体"};


    public DsbieJFrame() {
        setIconImage(ImageLoadUtil.getInstance().getLogoIcon().getImage());
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
        setVisible(true);
        validate();
    }

    private void init() {
        initMenu();
        initRootJSplitPane();
    }

    private void initRootJSplitPane() {
        CompletableFutureUtil.submit(() -> {
            rootJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            rootJSplitPane.setDividerSize(2);
            JScrollPane jTreeScrollPane = initTree();
            rootJSplitPane.setLeftComponent(jTreeScrollPane);

            JScrollPane jPanelScrollPane = initPanel();
            rootJSplitPane.setRightComponent(jPanelScrollPane);

            SwingUtilities.invokeLater(() -> {
                add(rootJSplitPane, BorderLayout.CENTER);
                validate();
            });
        });
    }


    private JScrollPane initPanel() {
        logoLabel = new JLabel(ImageLoadUtil.getInstance().getLogoIcon());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(logoLabel, BorderLayout.CENTER);

        JScrollPane jPanelScrollPane = new JScrollPane(jPanel);
        jPanelScrollPane.setMinimumSize(new Dimension(200, 0));
        return jPanelScrollPane;
    }


    private JScrollPane initTree() {
        jTree = LeftTree.getInstance().getJTree();
        JScrollPane jTreeScrollPane = new JScrollPane(jTree);
        jTreeScrollPane.setMinimumSize(new Dimension(200, 0));
        return jTreeScrollPane;
    }

    private void initMenu() {
        CompletableFutureUtil.submit(() -> {
            jMenuBar = new JMenuBar();

            JMenu fileMenu = new JMenu("文件");
            JMenu editMenu = new JMenu("编辑");
            JMenu settingsMenu = new JMenu("设置");
            JMenu helpMenu = new JMenu("帮助");
            jMenuBar.add(fileMenu);
            jMenuBar.add(editMenu);
            jMenuBar.add(settingsMenu);
            jMenuBar.add(helpMenu);

            JMenuItem exitMenu = new JMenuItem("退出");
            exitMenu.setIcon(ImageLoadUtil.getInstance().getExitIcon());
            exitMenu.addActionListener(e -> {
                log.info("程序退出");
                dispose();
            });
            fileMenu.add(exitMenu);

            JMenuItem renameMenu = new JMenuItem("重命名");
            renameMenu.setIcon(ImageLoadUtil.getInstance().getRenameIcon());
            renameMenu.addActionListener(new FrameJPopupMenu.RenameFolderAction());
            editMenu.add(renameMenu);

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

            JMenuItem about = new JMenuItem("关于");
            about.setIcon(ImageLoadUtil.getInstance().getAboutIcon());
            helpMenu.add(about);

            setAboutAction(about);
            initFontMenu(fontNameMenu, fontSizeMenu, fontStyleMenu);

            SwingUtilities.invokeLater(() -> {
                add(jMenuBar, BorderLayout.NORTH);
                validate();
            });
        });
    }

    private void initFontMenu(JMenu fontNameMenu, JMenu fontSizeMenu, JMenu fontStyleMenu) {
        Properties properties = KToolsContext.getInstance().getProperties();
        String fontName = String.valueOf(properties.get("font.name"));
        Integer fontSize = Integer.parseInt(String.valueOf(properties.get("font.size")));
        String fontStyle = String.valueOf(properties.get("font.style"));

        String[] allFonts = FontUtil.getAllFonts();

        ButtonGroup fontNameGroup = new ButtonGroup();
        for (String fontItem : allFonts) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(fontItem);
            if (Objects.equals(fontName, fontItem)) {
                jCheckBoxMenuItem.setSelected(true);
            }

            jCheckBoxMenuItem.addActionListener(e -> CompletableFutureUtil.submit(() -> {
                Properties properties1 = KToolsContext.getInstance().getProperties();
                Integer fontSize1 = Integer.parseInt(String.valueOf(properties1.get("font.size")));
                String fontStyle1 = String.valueOf(properties1.get("font.style"));

                String newFontName = jCheckBoxMenuItem.getText();
                KToolsContext.getInstance().getApi(SystemApi.class).saveOrUpdateProp("font.name", newFontName);

                log.info("修改字体名称为: {}", newFontName);
                SwingUtilities.invokeLater(() -> FontUtil.updateUIFont(new Font(newFontName, FontUtil.getFontStyle(fontStyle1), fontSize1)));
            }));

            fontNameGroup.add(jCheckBoxMenuItem);
            fontNameMenu.add(jCheckBoxMenuItem);
        }

        ButtonGroup fontSizeGroup = new ButtonGroup();
        for (int i = 10; i <= 30; i++) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(i + "");
            if (Objects.equals(fontSize, i)) {
                jCheckBoxMenuItem.setSelected(true);
            }

            jCheckBoxMenuItem.addActionListener(e -> CompletableFutureUtil.submit(() -> {
                Properties properties12 = KToolsContext.getInstance().getProperties();
                String fontName1 = String.valueOf(properties12.get("font.name"));
                String fontStyle12 = String.valueOf(properties12.get("font.style"));

                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                int newFontSize = Integer.parseInt(source.getText());

                KToolsContext.getInstance().getApi(SystemApi.class).saveOrUpdateProp("font.size", String.valueOf(newFontSize));

                log.info("修改字体大小为: {}", newFontSize);
                SwingUtilities.invokeLater(() -> FontUtil.updateUIFont(new Font(fontName1, FontUtil.getFontStyle(fontStyle12), newFontSize)));
            }));
            fontSizeGroup.add(jCheckBoxMenuItem);
            fontSizeMenu.add(jCheckBoxMenuItem);
        }

        ButtonGroup fontStyleGroup = new ButtonGroup();
        for (String style : fontStyleArr) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(style);
            if (Objects.equals(style, fontStyle)) {
                jCheckBoxMenuItem.setSelected(true);
            }

            jCheckBoxMenuItem.addActionListener(e -> CompletableFutureUtil.submit(() -> {
                Properties properties13 = KToolsContext.getInstance().getProperties();
                String fontName12 = String.valueOf(properties13.get("font.name"));
                Integer fontSize12 = Integer.parseInt(String.valueOf(properties13.get("font.size")));

                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                String newFontStyle = source.getText();

                KToolsContext.getInstance().getApi(SystemApi.class).saveOrUpdateProp("font.style", newFontStyle);

                log.info("修改字体样式为: {}", newFontStyle);
                SwingUtilities.invokeLater(() -> FontUtil.updateUIFont(new Font(fontName12, FontUtil.getFontStyle(newFontStyle), fontSize12)));
            }));
            fontStyleGroup.add(jCheckBoxMenuItem);
            fontStyleMenu.add(jCheckBoxMenuItem);
        }
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
