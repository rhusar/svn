

/*
 * Created on Wed Jun 25 18:58:26 IST 2008
 *
 * Generated by GEMS 
 */
 
package org.smartfrog.authoringtool;

import org.gems.designer.ElementGroup;
import org.gems.designer.ElementGroupImpl;
import org.gems.designer.ConstraintsChecker;
import org.gems.designer.GenericElementFactory;
import org.gems.designer.GraphicsProvider;
import org.gems.designer.LabelProvider;
import org.gems.designer.ModelElementFactory;
import org.gems.designer.ModelProvider;
import org.gems.designer.ModelRepository;
import org.gems.designer.PaletteProvider;
import org.gems.designer.model.Atom;
import org.gems.designer.model.Model;
import org.eclipse.emf.ecore.EPackage;


/**
 * 
 */
 
public class SmartfrogProvider implements ModelProvider, org.gems.designer.model.EMFModelProvider{
    public static final String MODEL_ID = "http://www.smartfrog.org/sfml";
    private GraphicsProviderImpl graphicsProvider_ = new GraphicsProviderImpl();
    private LabelProviderImpl labelProvider_ = new LabelProviderImpl();
    private GenericElementFactory factory_ = new GenericElementFactory();
    private SmartfrogPaletteProvider paletteProvider_ = new SmartfrogPaletteProvider();
    private SmartfrogConstraintsChecker checker_ = new SmartfrogConstraintsChecker();
    private static SmartfrogProvider instance_;
    
    
    public static SmartfrogProvider getInstance(){
    	if(instance_ == null){
    		instance_ = new SmartfrogProvider();
        	ModelRepository.getInstance().registerModelProvider(instance_);
    	}
    	return instance_;
    }
    
	private class RootModel extends Model{
		
		public Atom[] getChildAtoms() {
			
			Atom[] atoms = {
				
			};
			return atoms;
		}
		public org.gems.designer.model.Model[] getChildModels() {
			
			Model[] models = {
			     
				   new Component(),
				   new Composite(),
				   new DependencyModel(),
				   new Attribute(),
				   new And(),
				   new Connectors(),
				   new Or(),
				   new Nor(),
				   new Nand()};
			return models;
		}
        public String getModelID() {
            return MODEL_ID;
        }
}
	

	/* (non-Javadoc)
	 * @see org.gems.designer.ModelProvider#getRootModel()
	 */
	public org.gems.designer.model.Model getRootModel() {
		
		return new RootModel();
	}

	/* (non-Javadoc)
	 * @see org.gems.designer.ModelProvider#getGraphicsProvider()
	 */
	public GraphicsProvider getGraphicsProvider() {
		
		return graphicsProvider_;
	}

	/* (non-Javadoc)
	 * @see org.gems.designer.ModelProvider#getLabelProvider()
	 */
	public LabelProvider getLabelProvider() {
		
		return labelProvider_;
	}

	/**
	 * 
	 */
	public SmartfrogProvider() {
		super();
	}

	public ModelElementFactory getModelElementFactory() {
		return factory_;
	}


    public String getModelID() {
        return MODEL_ID;
    }
    
    public ConstraintsChecker getConstraintsChecker() {
        return checker_;
    }
    
    public PaletteProvider getPaletteProvider(){
        return paletteProvider_;
    }
    
    public ElementGroup[] getElementGroups() {
		ElementGroup[] els = {
			new ElementGroupImpl(getRootModel().getChildAtoms(),"Atoms"),
			new ElementGroupImpl(getRootModel().getChildModels(),"Models")
		};
		return els;
	}
	
	public EPackage getEMFPackage(){
		return org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl.eINSTANCE;
	}
 
}

