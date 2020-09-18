const express = require('express');
const cors = require('cors');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

app.use(cors());

app.use(express.static(__dirname + '/dist/web'));

// Proxy API
var proxyOptions = {
    target: 'http://easyorienteering-backend.herokuapp.com', 
    changeOrigin: true, // No funciona
    onProxyRes: function (proxyRes, req, res) {
        proxyRes.headers['Access-Control-Allow-Origin'] = '*';
    }
}
app.use('/api', createProxyMiddleware(proxyOptions));

// Peticiones web
app.get('/*', function(req, res) {
    res.sendFile('index.html', {root: 'dist/web/'});
});

app.listen(process.env.PORT || 4200);