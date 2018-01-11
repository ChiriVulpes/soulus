package yuudaari.soulus.common.util;

import yuudaari.soulus.common.compat.JeiDescriptionRegistry;

public interface IProvidesJeiDescription {
	void onRegisterDescription(JeiDescriptionRegistry registry);
}