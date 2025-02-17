export default {
  'en': {
    totalSelect: 'Select Dimensions:',
    editDimension: 'Edit Dimensions',
    name: 'Name',
    type: 'Type',
    tableAlias: 'Table Alias',
    column: 'Column',
    datatype: 'Data Type',
    cardinality: 'Cardinality',
    comment: 'Comment',
    reset: 'Reset',
    computedColumns: 'Computed Columns',
    expression: 'Expression',
    sqlOutput: 'Dimensions Suggestion',
    outputTipOne: 'Dimensions suggested based on inputed SQL patterns.',
    resetTip: 'Reset will call last saving back and overwrite existing dimensions. Are you sure you want to continue?',
    dataOriented: 'Data Oriented',
    mix: 'Mix',
    businessOriented: 'Business Oriented',
    noCoincide: 'On the business oriented preference, you are suggested to use most dimensions from SQL patterns. Otherwise, optimizer can barely offer useful suggestion.',
    searchColumn: 'Search column name',
    sameName: 'The name of dimension can\'t be duplicated within the same model',
    sameNameTip: 'Dimension name is invalid or the same name already exists in this model. Please rename and try again',
    nameTip: 'This dimension name will be used in BI tools.',
    cardinalityTip: 'Total amount of unique data in this column. Could be gathered from sampling.',
    commentTip: 'Synced from the source table.',
    syncName: 'Sync Comments to Name',
    resetSyncName: 'Undo Sync',
    lockLookupTableTip: 'Unable to use columns from this table for dimensions. Because the join relationship of this dimension table  won\'t be precomputed.',
    useCCBylockLookupTableTip: 'Unable to use certain computed columns. Because the join relationships of related dimension tables won\'t be precomputed.',
    streamTips: 'For fusion model, the time partition column can’t be deleted from the dimension.',
    secStorTips: 'When the tiered storage is ON, the time partition column can\'t be deleted from the dimension.',
    textRecognition: 'Text Recognition',
    textRecognitionTips: 'Batch selection of columns by automatic recognition of pasted text'
  }
}
