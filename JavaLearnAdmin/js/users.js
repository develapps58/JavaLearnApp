/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var Users = function () {
    var self = this;
    
    var bearerAuthInfo = '';
    var basicAuthInfo = '';
    var userData = {};
    
    
    this.SetBasicAuthData = function (basic) {
        basicAuthInfo = basic;
    };
    
    this.SetBearerAuthData = function (bearer) {
        bearerAuthInfo = bearer;
    };
    
    this.SetUserData = function (data) {
        userData = data;
    };
    
    this.GetUserData = function () {
        return userData;
    };
    
    this.IsAuth = function () {
        if(bearerAuthInfo === '' && basicAuthInfo === '') return false;
        return true;
    };
    
    this.GetAuthInfo = function () {
        if(bearerAuthInfo !== '') return bearerAuthInfo;
        if(basicAuthInfo !== '') return basicAuthInfo;
        return false;
    };
    
    Users = function () {
        return self;
    };
};
