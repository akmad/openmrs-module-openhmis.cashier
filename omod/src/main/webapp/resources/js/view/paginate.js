define(
	[
		'lib/jquery',
		'lib/underscore',
		'lib/backbone',
		'lib/i18n',
		'openhmis'
	],
	function($, _, Backbone, i18n, openhmis) {
		/**
		 * PaginateView
		 *
		 * Show pagination status and controls based on a given
		 * GenericCollection.
		 */ 
		openhmis.PaginateView = Backbone.View.extend({
			tmplFile: "paginate.html",
			tmplSelector: "#pagination",
			pageSizes: [ 5, 10, 25, 50, 100 ],
			initialize: function(options) {
				_.bindAll(this, "changePageSize");
				this.options = {
					numberOfPages: 5,
					pageSize: 10
				}
				if (options) {
					if (options.numberOfPages)
						this.options.numberOfPages = options.numberOfPages;
					if (options.pageSize)
						this.options.pageSize = options.pageSize;
				}
				this.template = this.getTemplate();
				this.pageSizeTemplate = this.getTemplate(null, "#pageSize");
				this.page = 1;
			},
			
			getPageSize: function() { return this.options.pageSize; },
			setPageSize: function(size) { this.options.pageSize = size; },
			
			getStartIndex: function() {
				return ((this.page - 1) * this.options.pageSize) + 1;
			},
			
			getItemRange: function() {
				var last = this.page * this.options.pageSize;
				return {
					first: ((this.page - 1) * this.options.pageSize) + 1,
					last: this.model.totalLength < last ? this.model.totalLength : last
				}
			},
			
			getMaxPageNum: function() {
				return Math.floor(this.model.totalLength / this.options.pageSize)
					+ (this.model.totalLength % this.options.pageSize === 0 ? 0 : 1);
			},
			
			getPageRange: function() {
				var maxPages = this.getMaxPageNum();
				var numberOfPages = this.options.numberOfPages > maxPages ? maxPages : this.options.numberOfPages;
				var even = numberOfPages % 2 === 0;
				var shift = 0;
				var curPage = this.page - 1; // zero-based
				var first = curPage - Math.floor(numberOfPages / 2) - (even ? 1 : 0);
				if (first < 0) shift = Math.abs(first);
				else {
					var last = first + numberOfPages;
					if (last > maxPages)
						shift = maxPages - last;
				}
				return {
					first: first + shift + 1, // 1-based
					last: first + shift + numberOfPages
				}
			},
			
			changePageSize: function(event) {
				this.setPageSize($(event.target).val());
				this.fetch();
			},
			
			fetch: function(options) {
				options = options ? options : {}
				if (options.page) {
					switch (options.page) {
						case "first":
							this.page = 1;
							break;
						case "previous":
							if (this.page - 1 >= 1)
								this.page--;
							break;
						case "next":
							if (this.page + 1 <= (this.model.totalLength / this.pageSize) + 1)
								this.page++;
							break;
						case "last":
							this.page = this.getMaxPageNum();
							break;
						default:
							this.page = options.page;
					}
				}
				options.queryString = openhmis.addQueryStringParameter(options.queryString, "startIndex=" + this.getStartIndex());
				options.queryString = openhmis.addQueryStringParameter(options.queryString, "limit=" + this.options.pageSize);
				if (!options.getOptions) this.trigger("fetch", options, this);
				return options;
			},
			
			render: function() {
				this.$el.html(this.template({
					list: this.model,
					page: this.page,
					itemRange: this.getItemRange(),
					pageRange: this.getPageRange(),
					__: i18n
				}));
				var self = this;
				this.$(".first").not(".ui-state-disabled").click(function() { self.fetch({ page: "first" }) });
				this.$(".previous").not(".ui-state-disabled").click(function() { self.fetch({ page: "previous" }) });
				this.$("span.pages span").not(".ui-state-disabled").click(function(event) { self.fetch({ page: parseInt($(event.target).text()) }) });
				this.$(".next").not(".ui-state-disabled").click(function() { self.fetch({ page: "next" }) });
				this.$(".last").not(".ui-state-disabled").click(function() { self.fetch({ page: "last" }) });
				return this;
			},
			
			getRenderedPageSizeEl: function(el) {
				el = el ? el : $('<span id="paginationPageSize"></span>');
				$(el).html(this.pageSizeTemplate({
					list: this.model,
					pageSize: this.options.pageSize,
					pageSizes: this.pageSizes,
					__: i18n
				}));
				$(el).change(this.changePageSize);
				return el;
			}
		});
		
		return openhmis;
	}
);