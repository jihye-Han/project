module.exports = function (app) {

    /* Page Setup */
    require('./httpPage')(app);

    /* API Setup */
    require('./httpAPI')(app);
}