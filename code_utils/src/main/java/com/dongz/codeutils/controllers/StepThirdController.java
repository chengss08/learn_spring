package com.dongz.codeutils.controllers;

import com.dongz.codeutils.entitys.db.Column;
import com.dongz.codeutils.entitys.db.Table;
import com.dongz.codeutils.utils.DataBaseUtils;
import com.dongz.codeutils.utils.PropertiesUtils;
import com.dongz.codeutils.utils.StringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.dongz.codeutils.utils.DataBaseUtils.resetIsSelected;

/**
 * @author dong
 * @date 2020/2/10 21:52
 * @desc
 */
public class StepThirdController extends BaseController{

    public Button forwardBtn;
    public Button nextBtn;
    public ListView entities;
    public ListView columns;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<CheckBox> collect = selectedTables.entrySet().stream().map(this::apply).collect(Collectors.toList());
        entities.getItems().addAll(collect);
    }

    private CheckBox apply(Map.Entry<String, Table> item) {
        CheckBox checkBox = new CheckBox();
        checkBox.setText(item.getValue().getClassName());
        if (selectedVos.containsKey(item.getValue().getClassName())) {
            checkBox.setSelected(true);
        }
        checkBox.setOnMouseClicked(this::clickTable);
        return checkBox;
    }

    private void clickTable(MouseEvent event) {
        CheckBox source = (CheckBox) event.getSource();
        Table table;
        if (selectedVos.containsKey(source.getText())) {
            table = selectedVos.get(source.getText());
            ObservableList items = columns.getItems();
            if (items != null && items.size() != 0 && table.getClassName().equals(columns.getId())) {
                selectedVos.remove(table.getClassName());
                columns.setItems(null);
                columns.setId(null);
            } else {
                source.setSelected(true);
                showColumns(table, false);
            }
        } else {
            table = selectedTables.get(source.getText()).clone();
            table.setExtendsBase(false);
            selectedVos.put(table.getClassName(), table);
            showColumns(table, true);
        }
    }

    public void showColumns(final Table table, boolean isReset) {
        List<CheckBox> collect = table.getColumns().stream().map(item -> {
            if (isReset) {
                resetIsSelected(item);
            }

            CheckBox checkBox = new CheckBox();
            checkBox.setText(item.getFieldName());
            checkBox.setId(table.getClassName());
            checkBox.setSelected(item.isSelected());
            checkBox.setOnMouseClicked(this::clickColumn);
            return checkBox;
        }).collect(Collectors.toList());
        columns.setItems(null);
        columns.setId(table.getClassName());
        columns.setItems(FXCollections.observableArrayList(collect));
    }

    private void clickColumn(MouseEvent event) {
        CheckBox source = (CheckBox) event.getSource();
        String talbeName = source.getId();
        String columnName = source.getText();
        Table table = selectedVos.get(talbeName);
        List<Column> columns = table.getColumns();
        columns.stream().filter(item -> item.getFieldName().equals(columnName)).forEach(item -> item.setSelected(!item.isSelected()));
    }


    public void forward() throws IOException {
        changeStep(forwardBtn, STEP2);
    }

    public void next() throws IOException {
        changeStep(forwardBtn, STEP4);
    }
}
