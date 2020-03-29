module.exports = function(app){

  app.get('/', function(req, res){
    //return all files in the directory fileServer
    var path = require('path');
    var fs = require('fs');

    var fileServerPath = path.join(__dirname, 'fileServer');

    var listTitleFiles = [];

    fs.readdir(fileServerPath, function (err, files) {

        if (err) {
            return res.end('Unable to scan directory: ' + err);
        }

        files.forEach(function (file) {
            listTitleFiles.push(file);
        });

        var jsonOutput = {
          files: listTitleFiles
        };
        res.json(jsonOutput);
    });
  });

  app.get('/:fileName', function(req, res){
    var fs = require('fs');
    var url = require('url');

    var fileName = req.params.fileName;
    if(fileName.search("../")!=-1){
      res.end("HTTP ERROR 503");
    }

    fs.readFile('fileServer/'+fileName, 'utf8', function(err, contents) {
        if(contents === undefined){
          res.end("HTTP ERROR 404");
        }
        else{
          res.end(contents);
        }
    });
  });

  app.post('/:fileName', function(req, res){
    var fs = require('fs');
    var newContentsOfFile = req.body.text;

    var fileName = req.params.fileName;
    if(fileName.search("../") !=-1){
      res.end("HTTP ERROR 503");
    }

    fs.writeFile('fileServer/'+fileName, newContentsOfFile, function(err){
      if(err){
        res.end("HTTP ERROR 503");
      } else{
        res.end(newContentsOfFile);
      }
    });
  });

};
