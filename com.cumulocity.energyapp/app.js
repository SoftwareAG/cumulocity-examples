Ext.require([
	'C8Y.ux.InventoryManagementPanel',
	'C8Y.ux.UserManagementPanel',
	'APP.view.Energy'
]);

C8Y.application({
    name        : 'APP',
    controllers : ['Energy'],
    items       : [
		{
			xtype	    : 'energypanel',
			menuOption	: 'Energy',
			iconCls		: 'iconGraph'
		},
        {
            xtype      : 'c8yinventorymanagement',
            iconCls    : 'iconInventory',
            menuOption : 'Inventory'   
        },
        {
           xtype       : 'c8yusermanagementpanel',
           menuOption  : 'Administration',
           iconCls     : 'iconManagement'
        }
    ]
});
