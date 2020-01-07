const {Store} = require("../models");
const utils = require("../helpers/utils");

const MODULE_NAME = '[Store Controller]';

function getStores(req, res) {
    Store.findAll()
        .then(stores => res.status(200).send(stores))
        .catch(error => res.status(500).send(error));
}

function postStore(req, res) {
    utils.setHeaders(res);

    const storeIn = req.body;

    return Store.create(storeIn)
        .then(store => res.status(201).send(store))
        .catch(error => res.status(400).send(error));
}

function getStoreById(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;

    Store.findByPk(idIn)
        .then(store => res.status(200).send(store))
        .catch(error => res.status(403).send(error));
}

function putStore(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;
    const storeIn = req.body;

    Store.findByPk(idIn).then(store => {
        if (!store) {
            res.status(401).send({});
        }
        return store.update(storeIn)
            .then(newStore => res.status(200).send(newStore))
            .catch(error => res.status(403).send(error));
    }).catch(error => console.log(error));
}

function delStore(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;

    Store.findByPk(idIn).then(store => {
        if (!store) {
            res.status(200).send({"success": 0, "description": "not found !"});
        } else {
            return store.destroy()
                .then(() => res.status(200).send({"success": 1, "description": "deleted!"}))
                .catch(() => res.status(403).send({"success": 0, "description": "error !"}))
        }
    }).catch(error => console.log(error));
}


module.exports = {
    getStores,
    postStore,
    getStoreById,
    putStore,
    delStore,
    MODULE_NAME
};
