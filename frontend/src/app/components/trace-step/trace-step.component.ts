import { CommonModule } from "@angular/common";
import { Component, Input } from "@angular/core";
import { Sha256Trace } from "../../models/trace.models";
import { RoundTableComponent } from "../round-table/round-table.component";

@Component({
  selector: "app-trace-step",
  standalone: true,
  imports: [CommonModule, RoundTableComponent],
  templateUrl: "./trace-step.component.html",
  styleUrls: ["./trace-step.component.css"],
})
export class TraceStepComponent {
  @Input({ required: true }) id = "";
  @Input({ required: true }) title = "";
  @Input({ required: true }) description = "";
  @Input({ required: true }) trace!: Sha256Trace;
  open = false;

  toggle(): void {
    this.open = !this.open;
  }

  preview(value: string, max = 180): string {
    return value.length > max ? `${value.slice(0, max)}…` : value;
  }
}
