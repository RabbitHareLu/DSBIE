package com.dsbie.frontend.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.Getter;

import javax.swing.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:39
 */
@Getter
public class ImageLoadUtil {

    private static final ImageLoadUtil INSTANCE = new ImageLoadUtil();

    private ImageIcon logoIcon = null;
    private ImageIcon aboutIcon = null;
    private ImageIcon newFolderIcon = null;
    private ImageIcon newIcon = null;
    private ImageIcon fontIcon = null;
    private ImageIcon fontNameIcon = null;
    private ImageIcon fontSizeIcon = null;
    private ImageIcon fontStyleIcon = null;
    private ImageIcon exitIcon = null;
    private ImageIcon newJdbcIcon = null;
    private ImageIcon folderCloseIcon = null;
    private ImageIcon deleteIcon = null;
    private ImageIcon refreshIcon = null;
    private ImageIcon renameIcon = null;
    private ImageIcon tableIcon = null;
    private ImageIcon closeTabbedIcon = null;
    private ImageIcon addRowIcon = null;
    private ImageIcon deleteRowIcon = null;
    private ImageIcon editIcon = null;
    private ImageIcon schemaIcon = null;
    private ImageIcon importIcon = null;
    private ImageIcon exportIcon = null;
    private ImageIcon tableStructExportIcon = null;
    private ImageIcon lineWrapIcon = null;
    private ImageIcon latestIcon = null;
    private ImageIcon chooseFileIcon = null;

    private ImageLoadUtil() {
        logoIcon = buildIcon("images/logo.svg");
        aboutIcon = buildIcon("images/about.svg");
        newFolderIcon = buildIcon("images/newFolder.svg");
        newIcon = buildIcon("images/new.svg");
        fontIcon = buildIcon("images/font.svg");
        fontNameIcon = buildIcon("images/fontName.svg");
        fontSizeIcon = buildIcon("images/fontSize.svg");
        fontStyleIcon = buildIcon("images/fontStyle.svg");
        exitIcon = buildIcon("images/exit.svg");
        newJdbcIcon = buildIcon("images/newJdbc.svg");
        folderCloseIcon = buildIcon("images/folderClose.svg");
        deleteIcon = buildIcon("images/delete.svg");
        refreshIcon = buildIcon("images/refresh.svg");
        renameIcon = buildIcon("images/rename.svg");
        tableIcon = buildIcon("images/table.svg");
        closeTabbedIcon = buildIcon("images/closeTabbed.svg");
        addRowIcon = buildIcon("images/addRow.svg");
        deleteRowIcon = buildIcon("images/deleteRow.svg");
        editIcon = buildIcon("images/edit.svg");
        schemaIcon = buildIcon("images/schema.svg");
        importIcon = buildIcon("images/import.svg");
        exportIcon = buildIcon("images/export.svg");
        tableStructExportIcon = buildIcon("images/tableStructExport.svg");
        lineWrapIcon = buildIcon("images/lineWrap.svg");
        latestIcon = buildIcon("images/latest.svg");
        chooseFileIcon = buildIcon("images/chooseFile.svg");
    }

    public static ImageLoadUtil getInstance() {
        return INSTANCE;
    }

    public FlatSVGIcon buildIcon(String name) {
        return new FlatSVGIcon(name);
    }
}
