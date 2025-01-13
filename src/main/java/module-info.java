module dtu.master_of_creatures
{
    // JavaFX libraries
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    // Maven libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires common;

    // Class allowances
    opens dtu.master_of_creatures to javafx.fxml;
    opens dtu.master_of_creatures.controllers to javafx.fxml;
    opens dtu.master_of_creatures.scenes to javafx.fxml;
    exports dtu.master_of_creatures;
    exports dtu.master_of_creatures.controllers;
    exports dtu.master_of_creatures.models;
    exports dtu.master_of_creatures.utilities;
    exports dtu.master_of_creatures.utilities.enums;
}