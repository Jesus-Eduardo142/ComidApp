'use strict';

module.exports = (sequelize, DataTypes) => {
  const Cart = sequelize.define('Cart', {
    UserId: DataTypes.INTEGER,
    items: DataTypes.ARRAY(DataTypes.INTEGER),
    total: DataTypes.DOUBLE,
    date: DataTypes.DATE
  }, {});
  Cart.associate = function(models) {
    Cart.belongsTo(models.User, {foreignKey: 'UserId', as: 'customer'});
  };
  return Cart;
};
