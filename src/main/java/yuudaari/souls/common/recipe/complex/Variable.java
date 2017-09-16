package yuudaari.souls.common.recipe.complex;

public class Variable {
	public String name;

	public Variable(String name) {
		if (!name.startsWith("$")) {
			throw new RuntimeException("Variable name '" + name + "' does not start with '$'");
		}
		this.name = name.substring(1);
	}
}