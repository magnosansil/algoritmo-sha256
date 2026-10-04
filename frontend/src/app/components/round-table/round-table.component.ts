import { CommonModule } from "@angular/common";
import { Component, Input } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Sha256Block } from "../../models/trace.models";

@Component({
  selector: "app-round-table",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./round-table.component.html",
  styleUrls: ["./round-table.component.css"],
})
export class RoundTableComponent {
  @Input() blocks: Sha256Block[] = [];
  selectedBlock = 0;
  showAfter = false;

  get block(): Sha256Block | undefined {
    return this.blocks[this.selectedBlock];
  }
}
