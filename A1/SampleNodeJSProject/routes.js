module.exports = function(app){

  //get operation
  app.get('/', function(req, res){
    res.render('index.ejs');
  });

  //post operation
  app.post('/post', function(req, res){
    res.render('postOperation.ejs');
  });

  //query parameters
  app.get('/queryParam', function(req, res){
    var queryParamA = req.query.a;
    res.render('queryParameter.ejs', {a: queryParamA});
  });

  //request headers
  app.get('/requestHeaders', function(req, res){
    //outputs request headers received
    var requestHeaders = JSON.stringify(req.headers);
    res.render('requestHeaders.ejs', {headers: requestHeaders});
  });

  //body of the request
  app.post('/sendBody', function(req, res){
    //shows the json data sent as body of request
    var jsonBody = JSON.stringify(req.body);
    console.log(req.body);
    res.render('sendBody.ejs', {body: jsonBody});
  });

  app.post('/sendBody2', function(req, res){
    res.json(req.body);
  });

  app.post('/layout_assignment', function(req, res){
    var jsonOutput = {
      "args":req.query,
      "data":JSON.stringify(req.body),
      "files":{},
      "form":{},
      "headers":req.headers,
      "json":req.body,
      "url":req.url
    };
    res.json(jsonOutput);
  });

  app.get('/layout_assignment', function(req, res){
    var jsonOutput = {
      "args":req.query,
      "headers":req.headers,
      "url":req.url
    };
    res.json(jsonOutput);
  })

};
