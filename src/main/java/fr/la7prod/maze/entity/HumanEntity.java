package fr.la7prod.maze.entity;

public abstract class HumanEntity {
	
	private String name;
	
	public HumanEntity() {}
	
	public HumanEntity(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof HumanEntity) && ((HumanEntity)o).name.equals(name);
	}
	
	@Override
	public String toString() {
		return "User(" + name + ")";
	}

}
