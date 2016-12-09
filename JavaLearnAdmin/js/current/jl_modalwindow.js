/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var JLModalWindow = function (title, content) {
    var mWindow = modalWindow();
    mWindow.init({
        background: "white",
        width: "80%",
        height: "80%",
        left: "10%",
        top: "10%",
        zIndex: "1001",
        border: "2px solid #3f3f3f",
        boxSizing: "border-box",
        movable: false,
        closable: true,
        headerStyle: {
            height: "25px",
            color: "white",
            background: "#3f3f3f",
            fontWeight: "bold",
            title: title
        },
        containerStyle: {
            textAlign: "left",
            overflowY: 'auto',
            boxSizing: "border-box"
        }
    });
    
    this.show = function () {
        mWindow.show();
        mWindow.append(content);
    };
};