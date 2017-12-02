Corpus: {
    documents: Documents[],
    initialLabels: Labels[], 
    numberOfTopics: number,
    topics: Topics[]
}

Session: {
    username: string,
    id: number,
    corpusId: number,
    progress: number,
    time: number, // in milliseconds?
    createdAt: date,
    labels: Label[],
    documents: Documents[]
}

Label: {
    id: number,
    parentId: number,
    name: string,
    documents: documentId[],
    color: string
}

LabelPrediction: {
}

LabelApplication: {
}

Document: {
    id: number,
    title: string,
    html: string,
    predictedLabel: labelId,
    appliedLabel: labelId
}

Topics: {
    id: number,
    documents: Document[],
    terms: Term[]
}

Term: {
    id: number,
    term: string,
    weight: number
}

Endpoint - /sessions
    GET - Session[]
    PUT - update Session
    POST - add Session

Endpoint - /sessions/:session_id
    GET - Session

Endpoint - /session/:session_id/labels
    GET - Label[]

Endpoint - /session/:session_id/labels/:label_id
    GET - Label
    PUT - update Label
    DELETE - delete Label
    POST - add Label
