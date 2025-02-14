/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Subtype</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Subtype#getName <em>Name</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Subtype#getBase <em>Base</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Subtype#getInstances <em>Instances</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Subtype#getLinks <em>Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getSubtype()
 * @model
 * @generated
 */
public interface Subtype extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * The default value is <code>"AnonymousSubtype"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getSubtype_Name()
   * @model default="AnonymousSubtype"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Subtype#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Base</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Base</em>' reference.
   * @see #setBase(ModelObject)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getSubtype_Base()
   * @model
   * @generated
   */
  ModelObject getBase();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Subtype#getBase <em>Base</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Base</em>' reference.
   * @see #getBase()
   * @generated
   */
  void setBase(ModelObject value);

  /**
   * Returns the value of the '<em><b>Instances</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.ModelObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Instances</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instances</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getSubtype_Instances()
   * @model type="org.smartfrog.authoringtool.emf.ModelObject" upper="2000"
   * @generated
   */
  EList getInstances();

  /**
   * Returns the value of the '<em><b>Links</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.SubtypeLink}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Links</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Links</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getSubtype_Links()
   * @model type="org.smartfrog.authoringtool.emf.SubtypeLink" containment="true" upper="20000"
   * @generated
   */
  EList getLinks();

} // Subtype
