const {Food} = require("../models");
const utils = require("../helpers/utils");

const MODULE_NAME = '[Food Controller]';

function getFoods(req, res) {
    Food.findAll()
        .then(foods => res.status(200).send(foods))
        .catch(error => res.status(500).send(error));
}

function postFood(req, res) {
    utils.setHeaders(res);

    const foodIn = req.body;

    return Food.create(foodIn)
        .then(food => res.status(201).send(food))
        .catch(error => res.status(400).send(error));
}

function getFoodById(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;

    Food.findByPk(idIn)
        .then(food => res.status(200).send(food))
        .catch(error => res.status(403).send(error));
}

function putFood(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;
    const foodIn = req.body;

    Food.findByPk(idIn).then(food => {
        if (!food) {
            res.status(401).send({});
        }
        return food.update(foodIn)
            .then(newStore => res.status(200).send(newStore))
            .catch(error => res.status(403).send(error));
    }).catch(error => console.log(error));
}

function delFood(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;

    Food.findByPk(idIn).then(food => {
        if (!food) {
            res.status(200).send({"success": 0, "description": "not found !"});
        } else {
            return food.destroy()
                .then(() => res.status(200).send({"success": 1, "description": "deleted!"}))
                .catch(() => res.status(403).send({"success": 0, "description": "error !"}))
        }
    }).catch(error => console.log(error));
}


module.exports = {
    getFoods,
    postFood,
    getFoodById,
    putFood,
    delFood,
    MODULE_NAME
};
