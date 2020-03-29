//APP
var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');

var port = 1337;
var app = express();
//setting app config. Path to html files is in 'views' bc of ejs.
app.set('view engine', 'ejs');
//path to files (css, js)
app.set('SampleNodeJSProject', path.join(__dirname, 'SampleNodeJSProject'));
app.use(express.static(__dirname + '/public'));

// see https://stackoverflow.com/questions/39870867/what-does-app-usebodyparser-json-do
// for more details on bodyparser configuration.

// support parsing of application/json type post data
app.use(bodyParser.json());

//support parsing of application/x-www-form-urlencoded post data.
//Warning: If commented out, some test for a request in Java will not work (body sent)
//app.use(bodyParser.urlencoded({ extended: true }));

require('./routes')(app);

app.listen(process.env.PORT || port);
