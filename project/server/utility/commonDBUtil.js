var systemConfig = require('../config/systemConfig');
var mysql = require('mysql');
var async = require('async');
const userDBUtil = require('./userDBUtil');
const cheerDBUtil = require('./cheerDBUtil');

module.exports.connect = function (next) {

    /* Initialize DB */
    global.db = mysql.createConnection({
        host: systemConfig.db.host,
        user: systemConfig.db.user,
        password: systemConfig.db.password,
        database: systemConfig.db.database,
        multipleStatements: true
    });

    global.db.connect(function (err) {

        if (err) {
            console.error(err);
            return next(false);
        }

        /* Initialize DB */
        async.waterfall([
            function (callback) {
                userDBUtil.createUserTable(function (result) {
                    if (result === true) {
                        callback(null);
                    } else {
                        callback("User DB initialization failed");
                    }
                });
            },
            function (callback) {
                cheerDBUtil.createCheerTable(function (result) {
                    if (result === true) {
                        callback(null);
                    } else {
                        callback("Cheer DB initialization failed");
                    }
                });
            },
        ], function (err) {
            if (err) {
                console.error(err);
                next(false);
            }
            else {
                next(true);
            }
        });
    });
}