const {User} = require("../models");
const utils = require("../helpers/utils");

const MODULE_NAME = '[Signup Controller]';

function getUsers(req, res) {
    const roleIn = req.swagger.params.role.value;

    const options = {
        where: {
            role: roleIn
        },
        attributes: {
            exclude: ['password']
        }
    };

    User.findAll(options)
        .then(users => res.status(200).send(users))
        .catch(error => res.status(500).send(error));
}

function postUser(req, res) {
    utils.setHeaders(res);

    const userIn = req.body;

    return User.create(userIn).then(user => res.status(201).send({
        "id": user.id,
        "username": user.username,
        "role": user.role,
        "city": user.city,
        "address": user.address,
        "cp": user.cp
    })).catch(error => res.status(400).send(error));
}

function getUserById(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;
    const options = {
        attributes: {
            exclude: ['password']
        }
    };

    User.findByPk(idIn, options)
        .then(user => res.status(200).send(user))
        .catch(error => res.status(403).send(error));
}

function putUser(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;
    const userIn = req.body;

    User.findByPk(idIn).then(user => {
        if (!user) {
            res.status(401).send({});
        }
        return user.update(userIn).then(newUser => res.status(200).send({
            "id": newUser.id,
            "username": newUser.username,
            "role": newUser.role,
            "city": newUser.city,
            "address": newUser.address,
            "cp": newUser.cp
        })).catch(error => res.status(403).send(error));
    }).catch(error => console.log(error));
}

function delUser(req, res) {
    utils.setHeaders(res);

    const idIn = req.swagger.params.id.value;

    User.findByPk(idIn).then(user => {
        if (!user) {
            res.status(200).send({"success": 0, "description": "not found !"});
        } else {
            return user.destroy()
                .then(() => res.status(200).send({"success": 1, "description": "deleted!"}))
                .catch(() => res.status(403).send({"success": 0, "description": "error !"}))
        }
    }).catch(error => console.log(error));
}


module.exports = {
    getUsers,
    postUser,
    getUserById,
    putUser,
    delUser,
    MODULE_NAME
};
