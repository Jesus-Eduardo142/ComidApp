'use strict';
module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    username: DataTypes.STRING,
    password: DataTypes.STRING,
    role: DataTypes.STRING,
    city: DataTypes.STRING,
    address: DataTypes.STRING,
    cp: DataTypes.STRING
  }, {});
  User.associate = function(models) {
    User.hasMany(models.Cart, {as: 'shopping_history'});
  };
  return User;
};
