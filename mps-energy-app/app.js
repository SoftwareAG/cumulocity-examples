Ext.require([
	'C8Y.ux.InventoryManagementPanel',
	'C8Y.ux.AdministrationPanel',
	'APP.view.Energy',
    'C8Y.app.neutral.Header',
    'C8Y.app.neutral.Footer'
]);

C8Y.application({
    name        : 'APP',
    controllers : ['Energy'],
    header: 'neutralheader',
    footer: 'neutralfooter',
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
           xtype       : 'c8yadministrationpanel',
           menuOption  : 'Administration',
           iconCls     : 'iconManagement'
        }
    ]
});