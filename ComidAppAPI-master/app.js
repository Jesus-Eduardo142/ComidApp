'use strict';

const SwaggerExpress = require('swagger-express-mw');
const swaggerTools = require("swagger-tools");
const YAML = require("yamljs");
const swaggerConfig = YAML.load("./api/swagger/swagger.yaml");
const app = require('express')();
const auth = require("./api/helpers/auth");

module.exports = app; // for testing

let config = {
    appRoot: __dirname // required config
};

SwaggerExpress.create(config, function (err, swaggerExpress) {
    if (err) {
        throw err;
    }

    // install middleware
    swaggerExpress.register(app);

    swaggerTools.initializeMiddleware(swaggerConfig, function (middleware) {
        app.use(middleware.swaggerMetadata());

        app.use(
            middleware.swaggerSecurity({
                Bearer: auth.verifyToken
            })
        );

        let routerConfig = {
            controllers: "./api/controllers",
            useStubs: false
        };

        app.use(middleware.swaggerRouter(routerConfig));

        app.use(middleware.swaggerUi());

    });

  const port = process.env.PORT || 10010;
  app.listen(port);

    if (swaggerExpress.runner.swagger.paths['/hello']) {
        console.log('try this:\ncurl http://127.0.0.1:' + port + '/hello?name=Scott');
    }
});
