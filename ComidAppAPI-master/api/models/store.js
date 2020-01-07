'use strict';
module.exports = (sequelize, DataTypes) => {
  const Store = sequelize.define('Store', {
    name: DataTypes.STRING,
    rfc: DataTypes.STRING,
    address: DataTypes.STRING,
    phone: DataTypes.STRING
  }, {});
  Store.associate = function(models) {
    Store.hasMany(models.Food, {as: 'products'});
  };
  return Store;
};
