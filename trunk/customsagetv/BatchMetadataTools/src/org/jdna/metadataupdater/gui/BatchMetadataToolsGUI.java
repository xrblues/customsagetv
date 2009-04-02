package org.jdna.metadataupdater.gui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.metadataupdater.MetadataUpdater;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.metadataupdater.Version;
import org.jdna.ui.SwingBindingUtils;

public class BatchMetadataToolsGUI extends JFrame {
    private class AutomaticUpdaterTask extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            progressBar.setIndeterminate(true);

            // get the parent folder for processing
            IMediaFolder parentFolder = null;
            List<IMediaResource> resources = new ArrayList<IMediaResource>();
            for (String f : updaterTool.getFiles()) {
                File file = new File(f);
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(getOwner(), "Failed to process file:  " + file.getAbsolutePath());
                } else {
                    resources.add(MediaResourceFactory.getInstance().createResource(file.toURI()));
                }
            }

            // put all the videos/folders in a virtual top level folder for
            // processing
            if (resources.size() == 1 && resources.get(0).getType() == IMediaFolder.TYPE_FOLDER) {
                parentFolder = (IMediaFolder) resources.get(0);
            } else {
                parentFolder = MediaResourceFactory.getInstance().createVirtualFolder("Videos", resources);
            }

            IMediaResourceVisitor updatedVisitor = new IMediaResourceVisitor() {
                public void visit(IMediaResource resource) {
                    updatedMetadataModel.add(resource);
                }
            };

            // Main visitor for automatic updating
            AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId(), updaterTool.getPersistence(), updaterTool.getPersistenceOptions(),null, updatedVisitor, new IMediaResourceVisitor() {
                public void visit(IMediaResource resource) {
                    missingTableModel.add(resource);
                }
            });
            
            IMediaResourceVisitor updater = null;
            
            if (config.isProcessMissingMetadataOnly()) {
                updater = new MissingMetadataVisitor(updaterTool.getPersistence(), autoUpdater);
            } else {
                updater = autoUpdater;
            }

            parentFolder.accept(updater, config.isRecurseFolders());

            System.out.println("Scan Complete.");
            done();
            progressBar.setIndeterminate(false);
            setProgress(0);
            progressBar.setString("Complete");

            return null;
        }

    }

    public static JFrame APPLICATION_FRAME;
    
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable      missingMetadataTable;
    JCheckBox                       recurse;
    JCheckBox                       refreshSage;
    JCheckBox                       autoUpdate;
    JCheckBox                       processMissingOnly;
    // JCheckBox reindex = new JCheckBox("Force rebuild of indexes");
    JCheckBox                       reindex;
    JFileChooser                    chooser;
    JTextField                      folder;
    JButton                         scan;
    JTextField                      mdProvider;
    JProgressBar                    progressBar;

    private MetadataUpdaterConfiguration config = null;
    private MetadataUpdater         updaterTool = null;
    private MediaResourceTableModel missingTableModel = new MediaResourceTableModel();
    private MediaResourceTableModel updatedMetadataModel = new MediaResourceTableModel();

    public BatchMetadataToolsGUI(MetadataUpdater updater) throws HeadlessException {
        this.updaterTool = updater;
        this.config=ConfigurationManager.getInstance().getMetadataUpdaterConfiguration();
        initComponents();
        updateValues();
    }

    public BatchMetadataToolsGUI() throws HeadlessException {
        initComponents();
        updateValues();
    }

    private void initComponents() {
        APPLICATION_FRAME = this;
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        setTitle("Batch Metadata Tools " + Version.VERSION);

        // init components
        recurse = SwingBindingUtils.createCheckBox("Recurse into sub directories", config, "setRecurseFolders", "isRecurseFolders");
        refreshSage = SwingBindingUtils.createCheckBox("Refresh SageTV after scan", config, "setRefreshSageTV", "isRefreshSageTV");
        autoUpdate = SwingBindingUtils.createCheckBox("Automatic Search and Update", config, "setAutomaticUpdate", "isAutomaticUpdate");
        reindex = SwingBindingUtils.createCheckBox("Force rebuild of indexes", config, "setRefreshIndexes", "isRefreshIndexes");
        processMissingOnly = SwingBindingUtils.createCheckBox("Only process files with no metadata", config, "setProcessMissingMetadataOnly", "isProcessMissingMetadataOnly");

        chooser = new JFileChooser();
        folder = new JTextField();
        scan = new JButton("Start Scan");
        mdProvider = new JTextField();
        progressBar = new JProgressBar();

        // setPreferredSize(new Dimension(800, 480));

        // setup the view
        getContentPane().setLayout(new MigLayout("", "[][grow][]"));

        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        mb.add(fileMenu);

        setJMenuBar(mb);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
        fileMenu.add(exit);

        JLabel l1 = new JLabel("Folder to Scan");
        JButton folderButton = new JButton("...");

        JLabel l2 = new JLabel("Metadata Providers");
        mdProvider.setEnabled(false);
        JButton mdProviderButton = new JButton("...");

        add(l1);
        add(folder, "growx");
        add(folderButton, "wrap");

        add(l2);
        add(mdProvider, "growx");
        add(mdProviderButton, "wrap");

        JPanel options = new JPanel(new MigLayout("wrap 3"));

        options.add(recurse);
        options.add(autoUpdate);
        options.add(processMissingOnly);
        options.add(reindex);
        options.add(refreshSage);

        add(options, "span, wrap");

        add(scan, "span, align right, wrap");

        JPanel updatedPanel = new JPanel(new MigLayout());
        JPanel skippedPanel = new JPanel(new MigLayout());

        jScrollPane1 = new javax.swing.JScrollPane();
        missingMetadataTable = new javax.swing.JTable(missingTableModel);
        jScrollPane1.setViewportView(missingMetadataTable);

        JScrollPane jScrollPane2 = new JScrollPane();
        JTable updateTable = new JTable(updatedMetadataModel);
        jScrollPane2.setViewportView(updateTable);

        updatedPanel.add(jScrollPane2, "span, width 100%, wrap");

        skippedPanel.add(jScrollPane1, "span, width 100%, wrap");
        JButton manualUpdate = new JButton("Manually Process Items");
        skippedPanel.add(manualUpdate, "span, align right");

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Updated", updatedPanel);
        tabs.addTab("Skipped", skippedPanel);

        add(tabs, "span, grow, wrap, gapy 30");

        add(progressBar, "span, align right");

        pack();

        // setup events
        final JFrame frame = this;
        folder.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                updaterTool.setFiles(new String[] { folder.getText() });
                ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setGuiFolderToScan(folder.getText());
                updateValues();
            }
        });

        folderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Showing File Chooser");
                chooser.setDialogTitle("Choose Media Folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int val = chooser.showOpenDialog(frame);
                if (val == chooser.APPROVE_OPTION) {
                    updaterTool.setFiles(new String[] { chooser.getSelectedFile().getAbsolutePath() });
                    ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setGuiFolderToScan(chooser.getSelectedFile().getAbsolutePath());
                    updateValues();
                }
            }
        });

        scan.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AutomaticUpdaterTask task = new AutomaticUpdaterTask();
                task.execute();
            }

        });

        mdProviderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProviderDialog pd = new ProviderDialog(ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId(), frame, "Metadata Providers");
                pd.setVisible(true);
                ConfigurationManager.getInstance().getMetadataConfiguration().setDefaultProviderId(pd.getProviderIdString());
                updateValues();
            }

        });
        
        // add the listener on the table
        MediaTableMouseListener l = new MediaTableMouseListener();
        missingMetadataTable.addMouseListener(l);
        updateTable.addMouseListener(l);

    }

    private void saveConfiguration() {
        System.out.println("Saving Configuration");
        try {
            ConfigurationManager.getInstance().updated(ConfigurationManager.getInstance().getMetadataUpdaterConfiguration());
            ConfigurationManager.getInstance().save();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void updateValues() {
        MetadataUpdaterConfiguration cfg = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration();
        if (updaterTool.getFiles() != null && updaterTool.getFiles().length > 0) {
            folder.setText(updaterTool.getFiles()[0]);
        }

        if (StringUtils.isEmpty(folder.getText()) && !StringUtils.isEmpty(cfg.getGuiFolderToScan())) {
            folder.setText(cfg.getGuiFolderToScan());
        }

        if (!StringUtils.isEmpty(ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId())) {
            mdProvider.setText(ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId());
        }

        if (StringUtils.isEmpty(folder.getText()) || StringUtils.isEmpty(mdProvider.getText())) {
            System.out.println("Disabling Button");
            scan.setEnabled(false);
        } else {
            scan.setEnabled(true);
        }

        saveConfiguration();
    }

    public static void runApp(final MetadataUpdater updater) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Running GUI App");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BatchMetadataToolsGUI(updater).setVisible(true);
            }
        });
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        MetadataUpdater md = new MetadataUpdater();
        md.setMetadataProvicer(IMDBMetaDataProvider.PROVIDER_ID);
        runApp(md);
    }

}
