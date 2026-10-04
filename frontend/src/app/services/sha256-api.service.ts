import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { Sha256Trace } from "../models/trace.models";

@Injectable({ providedIn: "root" })
export class Sha256ApiService {
  private readonly endpoint = `${environment.apiUrl}/hash`;

  constructor(private readonly http: HttpClient) {}

  calculate(message: string): Observable<Sha256Trace> {
    return this.http.post<Sha256Trace>(this.endpoint, { message });
  }
}
