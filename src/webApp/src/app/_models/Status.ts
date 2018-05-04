export enum Status {
  NOTHING = 0,
  PROCESS = 1,
  SUCCESS = 2,
  ERROR = 3

}

export namespace Status {
  export function successOrError(b: boolean): Status {
    return b ? Status.SUCCESS : Status.ERROR;
  }

  export function isDone(status: Status): boolean {
    return status === Status.SUCCESS || status === Status.ERROR;
  }
}
