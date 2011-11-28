Ext.define('APP.controller.Energy', {
    extend  : 'Ext.app.Controller',
    
    refs	: [
        {
            ref: 'mpsTree',
            selector: 'energypanel #mpsTree'
        },
        {
            ref: 'mpsGraph',
            selector: 'energypanel #mpsGraph'
        },
        {
            ref: 'deviceControl',
            selector :'energypanel c8ydevicecontrolpanel'
        }
    ],

	init 	: function() {
		this.control({
			'energypanel #mpsTree' : {
				selectmo	: this.onSelectMps
			}
		});
	},
	
	onSelectMps : function(recordMo) {
		var graph = this.getMpsGraph(),
		    deviceControl = this.getDeviceControl();
		    
		graph.loadData({
			source : recordMo.get('id')
		});
		
		deviceControl.setManagedObject(recordMo);
	}
});