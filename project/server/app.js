const async = require('async');

async.waterfall([

    /* Initialize DB */
    function (callback) {
        require('./utility/commonDBUtil').connect((result) => {
            if (result == true) {
                console.log("DB connection successful");
                callback(null);
            }
            else {
                callback("Failed to Initailize DB");
            }
        });
    },
    /* Initialize WebService */
    function (callback) {

        require('./services/webService')();

        callback(null);
    },
], function (err) {
    if (err) {
        console.log(err);
        process.exit(1);
    }
}); 