/*
 * Created on May 17, 2005
 *
 * @author Fabio Zadrozny
 */
package org.python.copiedfromeclipsesrc;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.python.pydev.plugin.PydevPlugin;
import org.python.pydev.ui.UIConstants;

/**
 * An abstract field editor that manages a list of input values. The editor displays a list containing the values, buttons for adding and
 * removing values, and Up and Down buttons to adjust the order of elements in the list.
 * <p>
 * Subclasses must implement the <code>parseString</code>,<code>createList</code>, and <code>getNewInputObject</code> framework
 * methods.
 * </p>
 * 
 * NOTE: COPIED only because we want removePressed to be protected
 */
public abstract class PythonListEditor extends FieldEditor {

    public static boolean USE_ICONS = true;

    /**
     * The list widget; <code>null</code> if none (before creation or after disposal).
     */
    protected Tree list;

    /**
     * The button box containing the Add, Remove, Up, and Down buttons; <code>null</code> if none (before creation or after disposal).
     */
    private Composite buttonBox;

    /**
     * The Add button.
     */
    private Button addButton;
    
    /**
     * The Auto-config button.
     */
    protected Button autoConfigButton;

    /**
     * The Remove button.
     */
    protected Button removeButton;

    /**
     * The Up button.
     */
    private Button upButton;

    /**
     * The Down button.
     */
    private Button downButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;

    /**
     * The image to be shown in each interpreter.
     */
    private Image imageInterpreter;

    /**
     * Creates a new list field editor
     */
    protected PythonListEditor() {
        if(USE_ICONS){
            imageInterpreter = PydevPlugin.getImageCache().get(UIConstants.PY_INTERPRETER_ICON);
        }
    }

    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    protected PythonListEditor(String name, String labelText, Composite parent) {
        this();
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    protected void autoConfigPressed() {
        String input = getNewInputObject(true);
        addNewInput(input);
    }
    
    /**
     * Notifies that the Add button has been pressed.
     */
    protected void addPressed() {
        String input = getNewInputObject(false);
        addNewInput(input);
    }

    
    private void addNewInput(String input) {
        if (input != null) {
            setPresentsDefaultValue(false);
            createInterpreterItem(input, input);
            selectionChanged();
        }
    }

    /**
     * Adds a new tree item to the interpreter tree.
     */
    protected void createInterpreterItem(String name, String executable) {
        TreeItem item = new TreeItem(list, SWT.NULL);
        item.setText(new String[]{name, executable});
        item.setImage(this.imageInterpreter);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) list.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     * 
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
        autoConfigButton = createPushButton(box, "Auto Config");//$NON-NLS-1$
        removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
    }

    /**
     * This method is not longer used!
     */
    protected String createList(String[] items){
        throw new RuntimeException("doLoad/doStore should be overridden (so that it's not needed)"); 
    }

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
//        data.heightHint = convertVerticalDLUsToPixels(button, IDialogConstants.BUTTON_HEIGHT);
        int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == autoConfigButton) {
                    autoConfigPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == upButton) {
                    upPressed();
                } else if (widget == downButton) {
                    downPressed();
                } else if (widget == list) {
                    selectionChanged();
                }
            }
        };
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        list = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        list.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected abstract void doLoad();

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected abstract void doLoadDefault();

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected abstract void doStore();

    /**
     * Notifies that the Down button has been pressed.
     */
    protected void downPressed() {
        swap(false);
    }

    /**
     * Returns this field editor's button box containing the Add, Remove, Up, and Down button.
     * 
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) {
        if (buttonBox == null) {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            createButtons(buttonBox);
            buttonBox.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    addButton = null;
                    autoConfigButton = null;
                    removeButton = null;
                    upButton = null;
                    downButton = null;
                    buttonBox = null;
                }
            });

        } else {
            checkParent(buttonBox, parent);
        }

        selectionChanged();
        return buttonBox;
    }

    /**
     * Returns this field editor's list control.
     * 
     * @param parent the parent control
     * @return the list control
     */
    public Tree getListControl(Composite parent) {
        if (list == null) {
            list = new Tree(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
            
            list.setHeaderVisible(true);
            TreeColumn column1 = new TreeColumn(list, SWT.LEFT);
            column1.setText("Name");
            column1.setWidth(200);
            TreeColumn column2 = new TreeColumn(list, SWT.LEFT);
            column2.setText("Location");
            column2.setWidth(200);
            
            
            list.setFont(parent.getFont());
            list.addSelectionListener(getSelectionListener());
            list.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    list = null;
                }
            });
        } else {
            checkParent(list, parent);
        }
        return list;
    }

    /**
     * Creates and returns a new item for the list.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @return a new item
     */
    protected abstract String getNewInputObject(boolean autoConfig);

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        return 2;
    }

    /**
     * Returns this field editor's selection listener. The listener is created if nessessary.
     * 
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null)
            createSelectionListener();
        return selectionListener;
    }

    /**
     * Returns this field editor's shell.
     * <p>
     * This method is internal to the framework; subclassers should not call this method.
     * </p>
     * 
     * @return the shell
     */
    protected Shell getShell() {
        if (addButton == null)
            return null;
        return addButton.getShell();
    }

    /**
     * This method is no longer used.
     */
    protected String[] parseString(String stringList){
        throw new RuntimeException("doLoad/doStore should be overridden (so that it's not needed)");
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    protected void removePressed() {
        setPresentsDefaultValue(false);
        TreeItem[] selection = list.getSelection();
        if (selection != null && selection.length > 0) {
            for(TreeItem t:selection){
                t.dispose(); //dispose of those items!
            }
            selectionChanged();
        }
    }

    /**
     * Notifies that the list selection has changed.
     */
    protected void selectionChanged() {
        int index = getSelectionIndex();
        int size = list.getItemCount();

        removeButton.setEnabled(index >= 0);
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }


    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    public void setFocus() {
        if (list != null) {
            list.setFocus();
        }
    }


    protected int getSelectionIndex() {
        if(this.list.getSelectionCount() != 1){
            return -1;
        }
        
        TreeItem[] selection = list.getSelection();
        int index = -1;
        if(selection != null && selection.length > 0){
            index = list.indexOf(selection[0]);
        }
        return index;
    }
    
    
    /**
     * Moves the currently selected item up or down.
     * 
     * @param up <code>true</code> if the item should move up, and <code>false</code> if it should move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0 && this.list.getSelectionCount() == 1) {
            TreeItem curr = list.getItem(index);
            TreeItem replace = list.getItem(target);
            
            //Just update the text!
            String col0 = replace.getText(0);
            String col1 = replace.getText(1);
            replace.setText(new String[]{curr.getText(0), curr.getText(1)});
            curr.setText(new String[]{col0, col1});
            
            list.setSelection(list.getItem(target));
        }
        selectionChanged();
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    protected void upPressed() {
        swap(true);
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getListControl(parent).setEnabled(enabled);
        addButton.setEnabled(enabled);
        autoConfigButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }
}
