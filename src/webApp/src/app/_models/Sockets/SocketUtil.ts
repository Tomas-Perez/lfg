import * as io from 'socket.io-client';
import {Observable} from 'rxjs/Observable';

export class SocketUtil {

  private uri: string;
  private socket: SocketIOClient.Socket;

  constructor(uri: string) { // 'http://localhost:8080/lfg/users'; // ws?
    this.uri = uri;
  }

  public initSocket(): void {
    this.socket = io(this.uri);
  }

  public send(message: any): void {
    this.socket.emit('message', message);
  }

  public onMessage(): Observable<any> {// TODO add message to SocketEvent enum and delet this.
    return new Observable<any>(observer => {
      this.socket.on('message', (data: any) => observer.next(data));
    });
  }

  public onEvent(event: string): Observable<any> {
    return new Observable<any>(observer => {
      this.socket.on(event, (data: any) => observer.next(data));
    });
  }
}
