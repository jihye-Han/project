var systemConfig = require('../config/systemConfig');
var express = require('express');
var ejs = require('ejs');
var app = express();

module.exports = function () {

    var path = require('path');
    var logger = require('morgan');
    var cookieParser = require('cookie-parser');
    var session = require('express-session');
    var bodyParser = require('body-parser');
    var helmet = require('helmet');

    /* View Engine Setup */
    app.set('view engine', 'ejs');
    app.set('views', path.join(__dirname, '../views'));

    /* Disable View Cache */
    app.disable('view cache');

    /* Minimize Html code for client */
    app.set('minimizeHtml', 'false');

    /* Etc Setup */
    app.use(logger('dev'));
    app.use(bodyParser.json({ limit: '50mb' }));
    app.use(bodyParser.urlencoded({ limit: '50mb', extended: true, parameterLimit: 50000 }));
    app.use(cookieParser());
    app.use(session({
        secret: '!!##@@CARROT@@##!!',
        resave: false,
        saveUninitialized: true,
        cookie: { maxAge: 2592000000 }
    }));

    app.use('/', express.static(path.join(__dirname, '../public')));
    app.locals.pretty = true;

    app.set('port', systemConfig.webServer.port);
    var server = app.listen(app.get('port'), function () {
        console.log('Express server listening on port ' + server.address().port);
    });

    /* Routes Setup */
    require('../routes/route')(app);

    require('../routes/webSocketAPI')(server);

    /* Error Handler */
    app.use(function (req, res, next) {
        var err = new Error('Not Found');
        err.status = 404;
        next(err);
    });

    // development error handler
    // will print stacktrace
    if (app.get('env') === 'development') {
        app.use(function (err, req, res, next) {
            res.status(err.status || 500);
            res.render('error', {
                message: err.message,
                error: err
            });
        });
    }

    // production error handler
    // no stacktraces leaked to user
    app.use(function (err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: {}
        });
    });
}