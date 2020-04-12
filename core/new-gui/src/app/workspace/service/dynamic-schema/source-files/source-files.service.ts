import { Injectable } from '@angular/core';
import { UserFileService } from '../../../../dashboard/service/user-file/user-file.service';
import { UserAccountService } from '../../../../dashboard/service/user-account/user-account.service';
import { DynamicSchemaService } from '../dynamic-schema.service';
import { OperatorSchema } from '../../../types/operator-schema.interface';
import { isEqual } from 'lodash';
import { OperatorPredicate } from '../../../types/workflow-common.interface';

const fileNameInJsonSchema = 'filePath';

@Injectable({
  providedIn: 'root'
})
export class SourceFilesService {

  constructor(
    private dynamicSchemaService: DynamicSchemaService,
    private userAccountService: UserAccountService,
    private userFileService: UserFileService
  ) {
      this.detectFileChanges();
      this.dynamicSchemaService.registerInitialSchemaTransformer((op, schema) => this.transformInitialSchema(op, schema));
  }

  private detectFileChanges(): void {
    this.userFileService.getFileChangeEvent().subscribe(
      () => {
        this.applyFileSchemaChange();
      }
    );
  }

  private applyFileSchemaChange(): void {
    // for each operator, try to apply schema propagation result
    Array.from(this.dynamicSchemaService.getDynamicSchemaMap().keys()).forEach(operatorID => {
      const currentDynamicSchema = this.dynamicSchemaService.getDynamicSchema(operatorID);
      // if operator input attributes are in the result, set them in dynamic schema
      if (currentDynamicSchema.jsonSchema.properties && fileNameInJsonSchema in currentDynamicSchema.jsonSchema.properties) {
        const newDynamicSchema: OperatorSchema = this.mutateOperatorFileName(currentDynamicSchema);
        if (! isEqual(currentDynamicSchema, newDynamicSchema)) {
          // this.resetAttributeOfOperator(operatorID);
          this.dynamicSchemaService.setDynamicSchema(operatorID, newDynamicSchema);
        }
      }
    });
  }

  private mutateOperatorFileName(operatorSchema: OperatorSchema): OperatorSchema {
    let newJsonSchema = operatorSchema.jsonSchema;
    if (this.userAccountService.isLogin()) {
      newJsonSchema = DynamicSchemaService.mutateProperty(newJsonSchema, fileNameInJsonSchema,
        () => ({ type: 'string', enum: this.userFileService.getFileArray().map(userFile => userFile.name) }));
    } else {
      newJsonSchema = DynamicSchemaService.mutateProperty(newJsonSchema, fileNameInJsonSchema,
        () => ({ type: 'string', enum: ['test', 'bbb'] }));
    }

    return {
      ...operatorSchema,
      jsonSchema: newJsonSchema
    };
  }

  private transformInitialSchema(operator: OperatorPredicate, schema: OperatorSchema): OperatorSchema {
    // change the tableName to a dropdown enum of available tables in the system
    if (schema.jsonSchema.properties && fileNameInJsonSchema in schema.jsonSchema.properties) {
      let newJsonSchema = schema.jsonSchema;
      if (this.userAccountService.isLogin()) {
        newJsonSchema = DynamicSchemaService.mutateProperty(newJsonSchema, fileNameInJsonSchema,
          () => ({ type: 'string', enum: this.userFileService.getFileArray().map(userFile => userFile.name) }));
      } else {
        newJsonSchema = DynamicSchemaService.mutateProperty(newJsonSchema, fileNameInJsonSchema,
          () => ({ type: 'string', enum: ['test', 'bbb'] }));
      }
      return {
        ...schema,
        jsonSchema: newJsonSchema
      };
    }
    return schema;
  }
}
