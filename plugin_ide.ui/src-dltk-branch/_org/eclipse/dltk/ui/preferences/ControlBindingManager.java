package _org.eclipse.dltk.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import melnorme.lang.ide.ui.LangUIPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import _org.eclipse.dltk.internal.ui.dialogs.StatusUtil;
import _org.eclipse.dltk.ui.dialogs.StatusInfo;
import _org.eclipse.dltk.ui.util.IStatusChangeListener;

/**
 */
public class ControlBindingManager<KEY> {
	final IStatusChangeListener changeListener;

	private Map<Button, KEY> checkBoxControls;
	private Map<Combo, KEY> comboControls;
	private final Map<Combo, IComboSelectedValueProvider> comboValueProviders = new IdentityHashMap<Combo, IComboSelectedValueProvider>();

	private DependencyManager dependencyManager;

	final IPreferenceDelegate<KEY> preferenceDelegate;
	private Map<Button, KEY> radioControls;

	private Map<Text, KEY> textControls;
	private final Map<Text, ITextConverter> textTransformers = new HashMap<Text, ITextConverter>();
	private ValidatorManager validatorManager;

	public static class DependencyMode {
	}

	public static final DependencyMode DEPENDENCY_INVERSE_SELECTION = new DependencyMode();

	public interface IComboSelectedValueProvider {
		String getValueAt(int index);

		int indexOf(String value);
	}

	public ControlBindingManager(IPreferenceDelegate<KEY> delegate,
			IStatusChangeListener listener) {
		this.checkBoxControls = new HashMap<Button, KEY>();
		this.comboControls = new HashMap<Combo, KEY>();
		this.textControls = new HashMap<Text, KEY>();
		this.radioControls = new HashMap<Button, KEY>();

		this.validatorManager = new ValidatorManager();
		this.dependencyManager = new DependencyManager();

		this.changeListener = listener;
		this.preferenceDelegate = delegate;
	}

	public void bindControl(final Combo combo, final KEY key) {
		bindControl(combo, key, new IComboSelectedValueProvider() {

			@Override
			public String getValueAt(int index) {
				return index >= 0 && index < combo.getItemCount() ? combo
						.getItem(index) : null;
			}

			@Override
			public int indexOf(String value) {
				final String[] items = combo.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].equals(value)) {
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(Combo combo, KEY key, final String[] itemValues) {
		bindControl(combo, key, new IComboSelectedValueProvider() {
			@Override
			public String getValueAt(int index) {
				return itemValues[index];
			}

			@Override
			public int indexOf(String value) {
				for (int i = 0; i < itemValues.length; i++) {
					if (itemValues[i].equals(value)) {
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(final Combo combo, final KEY key,
			final IComboSelectedValueProvider itemValueProvider) {
		if (key != null) {
			comboControls.put(combo, key);
		}
		comboValueProviders.put(combo, itemValueProvider);

		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = combo.getSelectionIndex();
				preferenceDelegate.setString(key,
						itemValueProvider.getValueAt(index));

				changeListener.statusChanged(StatusInfo.OK_STATUS);
			}
		});
	}

	public void bindControl(final Button button, final KEY key, Control[] slaves) {
		if (key != null) {
			checkBoxControls.put(button, key);
		}

		createDependency(button, slaves);

		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean state = button.getSelection();
				preferenceDelegate.setBoolean(key, state);

				updateStatus(StatusInfo.OK_STATUS);
			}
		});
	}

	public void bindControl(final Text text, final KEY key,
			IFieldValidator validator) {
		bindControl(text, key, validator, null);
	}

	public void bindControl(final Text text, final KEY key,
			IFieldValidator validator, final ITextConverter transformer) {
		if (key != null) {
			if (textControls.containsKey(key)) {
				final RuntimeException error = new IllegalArgumentException(
						"Duplicate control " + key); //$NON-NLS-1$
				LangUIPlugin.logInternalError(error);
			}

			textControls.put(text, key);
			if (transformer != null) {
				textTransformers.put(text, transformer);
			}
		}

		if (validator != null) {
			validatorManager.registerValidator(text, validator);
		}

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				IStatus status = validateText(text);

				if (key != null) {
					if (status.getSeverity() != IStatus.ERROR) {
						String value = text.getText();
						if (transformer != null) {
							value = transformer.convertInput(value);
						}

						preferenceDelegate.setString(key, value);
					}
				}

				updateStatus(status);
			}
		});
	}

	public void bindRadioControl(final Button button, final KEY key,
			final Object enable, Control[] dependencies) {
		if (key != null) {
			radioControls.put(button, key);
		}

		createDependency(button, dependencies);

		button.setData(String.valueOf(enable));
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				String value = String.valueOf(enable);
				preferenceDelegate.setString(key, value);
			}
		});
	}

	public void createDependency(final Button button, Control[] dependencies) {
		createDependency(button, dependencies, null);
	}

	public void createDependency(final Button button, Control[] dependencies,
			DependencyMode mode) {
		if (dependencies != null) {
			dependencyManager.createDependency(button, dependencies, mode);
		}
	}

	public IStatus getStatus() {
		IStatus status = StatusInfo.OK_STATUS;
		Iterator<Text> iter = textControls.keySet().iterator();
		while (iter.hasNext()) {
			IStatus s = validateText(iter.next());
			status = StatusUtil.getMoreSevere(s, status);
		}

		return status;
	}

	public void initialize() {
		initTextControls();
		initCheckBoxes();
		initRadioControls();
		initCombos();

		dependencyManager.initialize();
	}

	protected void updateStatus(IStatus status) {
		if (!status.matches(IStatus.ERROR)) {
			Iterator<Text> iter = textControls.keySet().iterator();
			while (iter.hasNext()) {
				IStatus s = validateText(iter.next());
				status = StatusUtil.getMoreSevere(s, status);
			}
		}

		changeListener.statusChanged(status);
	}

	private void initCheckBoxes() {
		Iterator<Button> it = checkBoxControls.keySet().iterator();
		while (it.hasNext()) {
			final Button button = it.next();
			final KEY key = checkBoxControls.get(button);
			button.setSelection(preferenceDelegate.getBoolean(key));
		}
	}

	private void initCombos() {
		for (final Map.Entry<Combo, KEY> entry : comboControls.entrySet()) {
			final Combo combo = entry.getKey();
			final KEY key = entry.getValue();
			final String value = preferenceDelegate.getString(key);
			final IComboSelectedValueProvider valueProvider = comboValueProviders
					.get(combo);
			if (valueProvider != null) {
				int index = valueProvider.indexOf(value);
				if (index >= 0) {
					combo.select(index);
				} else {
					combo.select(0);
				}
			}
		}
	}

	private void initRadioControls() {
		Iterator<Button> it = radioControls.keySet().iterator();
		while (it.hasNext()) {
			Button button = it.next();
			KEY key = radioControls.get(button);

			String enable = (String) button.getData();
			String value = preferenceDelegate.getString(key);

			if (enable != null && enable.equals(value)) {
				button.setSelection(true);
			} else {
				button.setSelection(false);
			}
		}
	}

	private void initTextControls() {
		Iterator<Text> it = textControls.keySet().iterator();
		while (it.hasNext()) {
			final Text text = it.next();
			final KEY key = textControls.get(text);
			String value = preferenceDelegate.getString(key);
			final ITextConverter textTransformer = textTransformers.get(text);
			if (textTransformer != null) {
				value = textTransformer.convertPreference(value);
			}

			text.setText(value);
		}
	}

	private IStatus validateText(Text text) {
		IFieldValidator validator = validatorManager.getValidator(text);
		if ((validator != null) && text.isEnabled()) {
			return validator.validate(text.getText());
		}

		return StatusInfo.OK_STATUS;
	}

	/**
     */
	class DependencyManager {
		private List<SelectionListener> masterSlaveListeners = new ArrayList<SelectionListener>();

		public void createDependency(final Button master,
				final Control[] slaves, final DependencyMode mode) {
			SelectionListener listener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean state = master.getSelection();
					// set enablement to the opposite of the selection value
					if (mode == DEPENDENCY_INVERSE_SELECTION) {
						state = !state;
					}

					for (int i = 0; i < slaves.length; i++) {
						slaves[i].setEnabled(state);
					}

					changeListener.statusChanged(StatusInfo.OK_STATUS);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}
			};

			master.addSelectionListener(listener);
			masterSlaveListeners.add(listener);
		}

		public void initialize() {
			Iterator<SelectionListener> it = masterSlaveListeners.iterator();
			while (it.hasNext()) {
				it.next().widgetSelected(null);
			}
		}
	}

	@SuppressWarnings("serial")
	static class ValidatorManager extends HashMap<Control, IFieldValidator> {

		public IFieldValidator getValidator(Control control) {
			return get(control);
		}

		public void registerValidator(Text text, IFieldValidator validator) {
			put(text, validator);
		}

		public void unregisterValidator(Text text) {
			remove(text);
		}

	}

}
