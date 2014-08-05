function AITile(position, type) {
  this.x = position.x;
  this.y = position.y;
  this.type = type;
}

AITile.prototype.duplicate = function(){
	return new AITile(this, this.type);
}

AITile.prototype.compareTo = function(other) {
	if (this.x != other.x){
		return this.x - other.x;
	} else if (this.y != other.y){
		return this.y - other.y;
	} else {
		return this.type - other.type;
	}
}

AITile.prototype.isEmpty = function() {
	return this.type == 0;
}

AITile.prototype.isNonEmpty = function () {
	return this.type != 0;
}

AITile.prototype.equals = function(tile) {
	return this.x == tile.x && this.y == tile.y && this.type == tile.type;
}

AITile.prototype.getTile = function(){
	return new Tile(this, this.type != 0 ? (1 << this.type) : 0);
}

AITile.prototype.toString = function(){
	return "[" + this.x + ", " + this.y + ", " + this.type + "]";
}