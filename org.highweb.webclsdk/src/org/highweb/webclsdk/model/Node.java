package org.highweb.webclsdk.model;

import java.util.ArrayList;
import java.util.List;

public class Node extends Model {
	protected List nodes;
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public Node() {
		nodes = new ArrayList();
	}
	
	private static class Adder implements IModelVisitor {

		/*
		 * @see ModelVisitorI#visitBoardgame(BoardGame)
		 */

		/*
		 * @see ModelVisitorI#visitBook(MovingBox)
		 */

		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox)
		 */

		/*
		 * @see ModelVisitorI#visitBoardgame(BoardGame, Object)
		 */
		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox, Object)
		 */
		public void visitNode(Node node, Object argument) {
			((Node) argument).addNode(node);
		}

	}

	private static class Remover implements IModelVisitor {
		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox, Object)
		 */
		public void visitNode(Node node, Object argument) {
			((Node) argument).removeNode(node);
			node.addListener(NullDeltaListener.getSoleInstance());
		}

	}
	
	public Node(String name, String type) {
		this();
		this.name = name;
		this.type = type;
	}
	
	public List getNodes() {
		return nodes;
	}
	
	public void addNode(Node tree) {
		nodes.add(tree);
		tree.parent = this;
		fireAdd(tree);
	}
	
	public void remove(Model toRemove) {
		toRemove.accept(remover, this);
	}
	
	public void removeNode(Node box) {
		nodes.remove(box);
		box.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(box);	
	}

	public void add(Model toAdd) {
		toAdd.accept(adder, this);
	}
	
	
	/** Answer the total number of items the
	 * receiver contains. */
	public int size() {
		return getNodes().size();
	}
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitNode(this, passAlongArgument);
	}

    public void clear() {
        nodes.clear();
    }

}
