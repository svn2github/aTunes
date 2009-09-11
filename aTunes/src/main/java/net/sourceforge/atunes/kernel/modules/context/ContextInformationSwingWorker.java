package net.sourceforge.atunes.kernel.modules.context;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * This class implements a special SwingWorker used to retrieve data from a ContextInformationDataSource and show it in a ContextPanelContent
 * @author alex
 *
 */
class ContextInformationSwingWorker extends SwingWorker<Map<String, ?>, Void> {

	/**
	 * Logger for all swing workers
	 */
	private static Logger logger;
	
	/**
	 * The context panel content where information must be shown after retrieving data
	 */
	private ContextPanelContent content;
	
	/**
	 * The context information data source used to retrieve information
	 */
	private ContextInformationDataSource dataSource;
	
	/**
	 * Parameters used to call data source
	 */
	private Map<String, ?> parameters;
	
	/**
	 * Constructor used to create a new ContextInformationSwingWorker
	 * @param content
	 * @param dataSource
	 * @param parameters
	 */
	ContextInformationSwingWorker(ContextPanelContent content, ContextInformationDataSource dataSource, Map<String, ?> parameters) {
		this.content = content;
		this.dataSource = dataSource;
		this.parameters = parameters;
	}
	
	@Override
	protected Map<String, ?> doInBackground() throws Exception {
		return dataSource.getData(parameters);
	}
	
	@Override
	protected void done() {
		super.done();
		try {
			content.updateContentWithDataSourceResult(get());
			// After update data expand content
			content.getParentTaskPane().setCollapsed(false);
		} catch (CancellationException e) {
			// thrown when cancelled
		} catch (InterruptedException e) {
			getLogger().error(LogCategories.CONTEXT, e);
		} catch (ExecutionException e) {
			getLogger().error(LogCategories.CONTEXT, e);
		}
	}
	
	/**
	 * Returns the logger shared by all workers
	 * @return
	 */
	private Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}
}
