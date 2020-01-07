'use strict';
module.exports = (sequelize, DataTypes) => {
  const Food = sequelize.define('Food', {
    name: DataTypes.STRING,
    description: DataTypes.STRING,
    quantity: DataTypes.INTEGER,
    price: DataTypes.DOUBLE,
    StoreId: DataTypes.INTEGER
  }, {});
  Food.associate = function(models) {
    Food.belongsTo(models.Store, {foreignKey: 'StoreId', as: 'seller'});
  };
  return Food;
};
