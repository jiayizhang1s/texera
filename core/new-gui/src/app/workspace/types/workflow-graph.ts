import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { OperatorPredicate, OperatorLink, OperatorPort } from './common.interface';
import { WorkflowGraphReadonly } from './workflow-graph-readonly.interface';
import { isEqual } from 'lodash-es';


/**
 * WorkflowGraph represents the Texera's logical WorkflowGraph,
 *  it's a graph consisted of operators <OperatorPredicate> and links <OpreatorLink>,
 *  each operator and link has its own unique ID.
 *
 */
export class WorkflowGraph implements WorkflowGraphReadonly {

  private operatorIDMap = new Map<string, OperatorPredicate>();
  private operatorLinkMap = new Map<string, OperatorLink>();

  private operatorAddSubject = new Subject<OperatorPredicate>();
  private operatorDeleteSubject = new Subject<{ deletedOperator: OperatorPredicate }>();
  private linkAddSubject = new Subject<OperatorLink>();
  private linkDeleteSubject = new Subject<{ deletedLink: OperatorLink }>();
  private operatorPropertyChangeSubject = new Subject<{ oldProperty: Object, operator: OperatorPredicate }>();

  constructor(
    operatorPredicates: OperatorPredicate[] = [],
    operatorLinks: OperatorLink[] = []
  ) {
    operatorPredicates.forEach(op => this.operatorIDMap.set(op.operatorID, op));
    operatorLinks.forEach(link => this.operatorLinkMap.set(link.linkID, link));
  }

  /**
   * Adds a new operator to the graph.
   * Throws an error the operator has a duplicate operatorID with an existing operator.
   * @param operator OperatorPredicate
   */
  public addOperator(operator: OperatorPredicate): void {
    WorkflowGraph.checkIfOperatorExists(this, operator);
    this.operatorIDMap.set(operator.operatorID, operator);
    this.operatorAddSubject.next(operator);
  }

  /**
   * Deletes the operator from the graph by its ID.
   * Throws an Error if the operator doesn't exist.
   * @param operatorID operator ID
   */
  public deleteOperator(operatorID: string): void {
    const operator = this.getOperator(operatorID);
    if (!operator) {
      throw new Error(`operator with ID ${operatorID} doesn't exist`);
    }
    this.operatorIDMap.delete(operatorID);
    this.operatorDeleteSubject.next({ deletedOperator: operator });
  }

  /**
   * Returns whether the operator exists in the graph.
   * @param operatorID operator ID
   */
  public hasOperator(operatorID: string): boolean {
    return this.operatorIDMap.has(operatorID);
  }

  /**
   * Gets the operator with the operatorID.
   * Throws an Error if the operator doesn't exist.
   * @param operatorID operator ID
   */
  public getOperator(operatorID: string): OperatorPredicate | undefined {
    return this.operatorIDMap.get(operatorID);
  }

  /**
   * Returns an array of all operators in the graph
   */
  public getOperators(): OperatorPredicate[] {
    return Array.from(this.operatorIDMap.values());
  }

  /**
   * Adds a link to the operator graph.
   * Throws an error if
   *  - the link already exists in the graph (duplicate ID or source-target)
   *  - the link is invalid (invalid source or target operator/port)
   * @param link
   */
  public addLink(link: OperatorLink): void {
    WorkflowGraph.checkIsValidLink(this, link);
    WorkflowGraph.checkIfLinkExists(this, link);
    this.operatorLinkMap.set(link.linkID, link);
    this.linkAddSubject.next(link);
  }

  /**
   * Deletes a link by the linkID.
   * Throws an error if the linkID doesn't exist in the graph
   * @param linkID link ID
   */
  public deleteLinkWithID(linkID: string): void {
    const link = this.getLinkWithID(linkID);
    if (!link) {
      throw new Error(`link with ID ${linkID} doesn't exist`);
    }
    this.operatorLinkMap.delete(linkID);
    this.linkDeleteSubject.next({ deletedLink: link });
  }

  /**
   * Deletes a link by the source and target of the link.
   * Throws an error if the link doesn't exist in the graph
   * @param source source port
   * @param target target port
   */
  public deleteLink(source: OperatorPort, target: OperatorPort): void {
    const link = this.getLink(source, target);
    if (!link) {
      throw new Error(`link from ${source.operatorID}.${source.portID}
        to ${target.operatorID}.${target.portID} doesn't exist`);
    }
    this.operatorLinkMap.delete(link.linkID);
    this.linkDeleteSubject.next({ deletedLink: link });
  }

  /**
   * Returns whether the graph contains the link with the linkID
   * @param linkID link ID
   */
  public hasLinkWithID(linkID: string): boolean {
    return this.operatorLinkMap.has(linkID);
  }

  /**
   * Returns wheter the graph contains the link with the source and target
   * @param source source operator and port of the link
   * @param target target operator and port of the link
   */
  public hasLink(source: OperatorPort, target: OperatorPort): boolean {
    const link = this.getLink(source, target);
    if (link === undefined) {
      return false;
    }
    return true;
  }

  /**
   * Returns a link with the linkID from operatorLinkMap.
   * Returns undefined if the link doesn't exist.
   * @param linkID link ID
   */
  public getLinkWithID(linkID: string): OperatorLink | undefined {
    return this.operatorLinkMap.get(linkID);
  }

  /**
   * Returns a link with the source and target from operatorLinkMap.
   * Returns undefined if the link doesn't exist.
   * @param source source operator and port of the link
   * @param target target operator and port of the link
   */
  public getLink(source: OperatorPort, target: OperatorPort): OperatorLink | undefined {
    const links = this.getLinks().filter(
      value => isEqual(value.source, source) && isEqual(value.target, target)
    );
    if (links.length === 0) {
      return undefined;
    }
    if (links.length > 1) {
      throw new Error(`WorkflowGraph inconsistency: find duplicate link with same source and target`);
    }
    return links[0];
  }

  /**
   * Returns an array of all the links in the graph.
   */
  public getLinks(): OperatorLink[] {
    return Array.from(this.operatorLinkMap.values());
  }

  /**
   * Sets the property of the operator to use the newProperty object.
   *
   * Throws an error if the operator doesn't exist.
   * @param operatorID operator ID
   * @param newProperty new property to set
   */
  public changeOperatorProperty(operatorID: string, newProperty: Object): void {
    const originalOperatorData = this.operatorIDMap.get(operatorID);
    if (originalOperatorData === undefined) {
      throw new Error(`operator with ID ${operatorID} doesn't exist`);
    }
    const oldProperty = originalOperatorData.operatorProperties;

    // constructor a new copy with new operatorProperty and all other original attributes
    const operator = {
      ...originalOperatorData,
      operatorProperties: newProperty,
    };
    // set the new copy back to the operator ID map
    this.operatorIDMap.set(operatorID, operator);

    this.operatorPropertyChangeSubject.next({ oldProperty, operator });
  }

  /**
   * Gets the observable event stream of an operator being added into the graph.
   */
  public getOperatorAddStream(): Observable<OperatorPredicate> {
    return this.operatorAddSubject.asObservable();
  }

  /**
 * Gets the observable event stream of an operator being deleted from the graph.
 * The observable value is the deleted operator.
 */
  public getOperatorDeleteStream(): Observable<{ deletedOperator: OperatorPredicate }> {
    return this.operatorDeleteSubject.asObservable();
  }

  /**
   *ets the observable event stream of a link being added into the graph.
   */
  public getLinkAddStream(): Observable<OperatorLink> {
    return this.linkAddSubject.asObservable();
  }

  /**
   * Gets the observable event stream of a link being deleted from the graph.
   * The observable value is the deleted link.
   */
  public getLinkDeleteStream(): Observable<{ deletedLink: OperatorLink }> {
    return this.linkDeleteSubject.asObservable();
  }

  /**
   * Gets the observable event stream of a link being deleted from the graph.
   * The observable value includes the old property that is replaced, and the operator with new property.
   */
  public getOperatorPropertyChangeStream(): Observable<{ oldProperty: Object, operator: OperatorPredicate }> {
    return this.operatorPropertyChangeSubject.asObservable();
  }

  /**
   * Checks if an operator with the same OperatorID already exists in the graph.
   * Throws an Error if the operator already exists.
   * @param graph
   * @param operator
   */
  public static checkIfOperatorExists(graph: WorkflowGraphReadonly, operator: OperatorPredicate): void {
    if (graph.hasOperator(operator.operatorID)) {
      throw new Error(`operator with ID ${operator.operatorID} already exists`);
    }
  }

  /**
   * Checks whether the link already exists in the graph by checking:
   *  - duplicate link ID
   *  - duplicate link source and target
   * Throws an Error if the link already exists.
   * @param graph
   * @param link
   */
  public static checkIfLinkExists(graph: WorkflowGraphReadonly, link: OperatorLink): void {
    if (graph.hasLinkWithID(link.linkID)) {
      throw new Error(`link with ID ${link.linkID} already exists`);
    }
    if (graph.hasLink(link.source, link.target)) {
      throw new Error(`link from ${link.source.operatorID}.${link.source.portID}
        to ${link.target.operatorID}.${link.target.portID} already exists`);
    }
  }

  /**
   * Checks if it's valid to add the given link to the graph.
   * Throws an Error if it's not a valid link because of:
   *  - invalid source operator or port
   *  - invalid target operator or port
   * @param graph
   * @param link
   */
  public static checkIsValidLink(graph: WorkflowGraphReadonly, link: OperatorLink): void {

    const sourceOperator = graph.getOperator(link.source.operatorID);
    if (!sourceOperator) {
      throw new Error(`link's source operator ${link.source.operatorID} doesn't exist`);
    }

    const targetOperator = graph.getOperator(link.target.operatorID);
    if (!targetOperator) {
      throw new Error(`link's target operator ${link.target.operatorID} doesn't exist`);
    }

    if (sourceOperator.outputPorts.find(
      (port) => port === link.source.portID) === undefined) {
      throw new Error(`link's source port ${link.source.portID} doesn't exist
          on output ports of the source operator ${link.source.operatorID}`);
    }
    if (targetOperator.inputPorts.find(
      (port) => port === link.target.portID) === undefined) {
      throw new Error(`link's target port ${link.target.portID} doesn't exist
          on input ports of the target operator ${link.target.operatorID}`);
    }
  }

}
