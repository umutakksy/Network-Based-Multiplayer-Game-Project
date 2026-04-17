module grup.proje {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;


    opens grup.proje to javafx.fxml;
    exports grup.proje;
    exports grup.proje.SocketTest;
    opens grup.proje.SocketTest to javafx.fxml;
    exports grup.proje.UI;
    opens grup.proje.UI to javafx.fxml;
    exports grup.proje.Controller;
    opens grup.proje.Controller to javafx.fxml;
    exports grup.proje.Lobby;
    opens grup.proje.Lobby to javafx.fxml;
}