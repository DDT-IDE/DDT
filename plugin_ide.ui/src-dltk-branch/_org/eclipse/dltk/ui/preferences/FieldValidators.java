package _org.eclipse.dltk.ui.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;

import _org.eclipse.dltk.ui.dialogs.StatusInfo;

public final class FieldValidators {

//	public static class FilePathValidator implements IFieldValidator {
//		@Override
//		public IStatus validate(String text) {
//			return validate(text, EnvironmentManager.getLocalEnvironment());
//		}
//
//		public IStatus validate(String text, IEnvironment environment) {
//			StatusInfo status = new StatusInfo();
//			if (environment == null) {
//				status
//						.setError(org.eclipse.dltk.ui.preferences.Messages.FieldValidators_0);
//				return status;
//			}
//
//			if (!(text.trim().length() == 0)) {
//				IFileHandle file = PlatformFileUtils
//						.findAbsoluteOrEclipseRelativeFile(environment, Path
//								.fromPortableString(text));
//
//				if (!file.exists()) {
//					status.setError(Messages.format(
//							ValidatorMessages.FilePathNotExists, text));
//				} else if (file.isDirectory()) {
//					status.setError(Messages.format(
//							ValidatorMessages.FilePathIsInvalid, text));
//				}
//			}
//
//			return status;
//		}
//
//		/**
//		 * @since 2.0
//		 */
//		public IStatus validate(URI location, IEnvironment environment) {
//			StatusInfo status = new StatusInfo();
//			if (environment == null) {
//				status
//						.setError(org.eclipse.dltk.ui.preferences.Messages.FieldValidators_0);
//				return status;
//			}
//			IFileHandle file = environment.getFile(location);
//			if (file == null || !file.exists()) {
//				status.setError(Messages.format(
//						ValidatorMessages.FilePathNotExists, location));
//			} else if (file.isDirectory()) {
//				status.setError(Messages.format(
//						ValidatorMessages.FilePathIsInvalid, location));
//			}
//			return status;
//		}
//	}

	public static class PositiveNumberValidator implements IFieldValidator {
		@Override
		public IStatus validate(String text) {
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0) {
				status.setError(ValidatorMessages.PositiveNumberIsEmpty);
			} else {
				try {
					int value = Integer.parseInt(text);
					if (value < 0) {
						status.setError(MessageFormat.format(ValidatorMessages.PositiveNumberIsInvalid, text));
					}
				} catch (NumberFormatException e) {
					status.setError(MessageFormat.format(ValidatorMessages.PositiveNumberIsInvalid, text));
				}
			}

			return status;
		}
	}

	public static class MinimumNumberValidator extends PositiveNumberValidator {
		private int minValue;

		public MinimumNumberValidator(int minValue) {
			this.minValue = minValue;
		}

		@Override
		public IStatus validate(String text) {
			StatusInfo status = (StatusInfo) super.validate(text);

			if (!status.isOK()) {
				return status;
			}

			int value = Integer.parseInt(text);
			if (value < minValue) {
				status.setError(MessageFormat.format(ValidatorMessages.MinValueInvalid, String.valueOf(minValue)));
			}

			return status;
		}
	}
//
//	public static class PortValidator implements IFieldValidator {
//		@Override
//		public IStatus validate(String text) {
//			StatusInfo status = new StatusInfo();
//
//			if (text.trim().length() == 0) {
//				status.setError(ValidatorMessages.PortIsEmpty);
//			} else {
//				try {
//					int value = Integer.parseInt(text);
//					if (value < 1000 || value > 65535) {
//						status.setError(Messages.format(
//								ValidatorMessages.PortShouldBeInRange, text));
//					}
//				} catch (NumberFormatException e) {
//					status.setError(Messages.format(
//							ValidatorMessages.PortShouldBeInRange, text));
//				}
//			}
//
//			return status;
//		}
//	}
//
//	// Available validators
//	public static IFieldValidator POSITIVE_NUMBER_VALIDATOR = new PositiveNumberValidator();
//	public static IFieldValidator PORT_VALIDATOR = new PortValidator();
}
