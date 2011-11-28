Ext.define('APP.view.Energy', {
	extend : 'Ext.panel.Panel',
	alias	: 'widget.energypanel',
	requires : [
		'C8Y.ux.InventoryTree',
		'C8Y.ux.MeasurementGraph',
		'C8Y.ux.DeviceControlPanel'
	],
	
	initComponent : function() {
		this.layout = 'border';
		this.bodyStyle="background:transparent;";
		this.items = this.buildItems();
		this.callParent();
	},
	
	buildItems : function() {
		return [
			{
				xtype	 			: 'c8yinventorytree',
				itemId				: 'mpsTree',
				region   			: 'west',
				width	 			: 280,
				managedObjectType 	: 'MPSEnergyMeter',
				childType			: 'childDevices',
				editable 			: false,
				plugins  			: [
					'c8ypanel'
				]
			},
			{
				xtype	 : 'c8ymeasurementgraph',
				itemId	 : 'mpsGraph',
				region	 : 'center',
				measurementProperty : 'com_cumulocity_model_energy_measurement_ThreePhaseElectricityMeasurement',
				ytitle		: 'Energy (kWh)',
				bodyStyle   : 'border-top-width: 0 !important;',
				dockedItems : {
				    xtype    : 'c8ydevicecontrolpanel',
				    dock    : 'bottom',
				    height  : 120,
				    title   : 'Device Control'
				},
				series	 : [
					{
						type 	: 'line',
						yField  : 'A+:1'
					},
					{
						type 	: 'line',
						yField  : 'A-:1'
					},
					{
						type 	: 'line',
						yField  : 'A+:2'
					},
					{
						type 	: 'line',
						yField  : 'A-:2'
					},
					{
						type 	: 'line',
						yField  : 'A+:3'
					},
					{
						type 	: 'line',
						yField  : 'A-:3'
					}
				],
				plugins  : [
					'c8ypanel'
				]
			}
		]
	}
});