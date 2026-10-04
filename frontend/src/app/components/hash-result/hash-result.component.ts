import { CommonModule } from "@angular/common";
import { Component, Input } from "@angular/core";

@Component({
  selector: "app-hash-result",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./hash-result.component.html",
  styleUrls: ["./hash-result.component.css"],
})
export class HashResultComponent {
  @Input() hash = "";
  copied = false;

  async copy(): Promise<void> {
    await navigator.clipboard.writeText(this.hash);
    this.copied = true;
    window.setTimeout(() => (this.copied = false), 1600);
  }
}
