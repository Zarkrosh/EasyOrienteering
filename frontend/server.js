const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

app.use(express.static(__dirname + '/dist/web'));

// Proxy API
var proxyOptions = {
    target: 'https://' + process.env.EASYO_BACKEND, 
    changeOrigin: true
}
app.use('/api', createProxyMiddleware(proxyOptions));

// Peticiones web
app.get('/*', function(req, res) {
    res.sendFile('index.html', {root: 'dist/web/'});
});

app.listen(process.env.PORT || 4200);